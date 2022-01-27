package org.tkit.quarkus.jpa.daos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.stream.Stream;

/**
 * The page query.
 *
 * @param <T> the entity class.
 */
public class PagedQuery<T> {
    /**
     * The logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(PagedQuery.class);
    /**
     * The entity manager.
     */
    private EntityManager em;

    /**
     * The search criteria.
     */
    private CriteriaQuery<T> criteria;

    /**
     * The search count criteria.
     */
    private CriteriaQuery<Long> countCriteria;

    /**
     * The current page.
     */
    private Page page;

    /**
     * Default constructor.
     *
     * @param em       the entity manager.
     * @param criteria the search criteria
     * @param page     the start page.
     */
    public PagedQuery(EntityManager em, CriteriaQuery<T> criteria, Page page, String idAttributeName) {
        this.em = em;
        this.criteria = setDefaultSorting(em, criteria, idAttributeName);
        this.page = page;
        this.countCriteria = createCountCriteria(em, criteria);
    }

    public PageResult<T> getPageResult() {
        try {
            // get count
            Long count = em.createQuery(countCriteria).getSingleResult();
            // get stream
            Stream<T> stream = em.createQuery(criteria)
                    .setFirstResult(page.number() * page.size())
                    .setMaxResults(page.size())
                    .getResultStream();
            // create page result
            return new PageResult<T>(count, stream, page);
        } catch (Exception ex) {
            String entityClass = criteria.getResultType() != null ? criteria.getResultType().getName() : null;
            throw new DAOException(Errors.GET_PAGE_RESULT_ERROR, ex, page.number(), page.size(), entityClass);
        }
    }

    private static <T> CriteriaQuery<T> setDefaultSorting(EntityManager em, CriteriaQuery<T> criteria, String idAttributeName) {
        Root<T> root = null;
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            if (criteria.getOrderList().isEmpty()) {
                log.warn("Paged query used without explicit orderBy. Ordering of results between pages not guaranteed. Please add an orderBy clause to your query.");
                root = findRoot(criteria, criteria.getResultType());
                if (root != null) {
                    criteria.orderBy(builder.asc(root.get(idAttributeName)));
                    log.warn("Default sorting by '{}' attribute is added.", idAttributeName);
                }
            }
        } catch (IllegalArgumentException ex) {
            log.error("There is no id attribute for Root:{}", root);
        }
        return criteria;
    }

    /**
     * Gets the current page.
     *
     * @return the current page.
     */
    public Page getPage() {
        return page;
    }

    /**
     * Gets the search count criteria.
     *
     * @return the search count criteria.
     */
    public CriteriaQuery<Long> countCriteria() {
        return countCriteria;
    }

    /**
     * Gets the search criteria.
     *
     * @return the search criteria.
     */
    public CriteriaQuery<T> criteria() {
        return criteria;
    }

    /**
     * Move to the previous page.
     *
     * @return the page query.
     */
    public PagedQuery<T> previous() {
        if (page.number() > 0) {
            page = Page.of(page.number() - 1, page.size());
        }
        return this;
    }

    /**
     * Move to the next page.
     *
     * @return the page query.
     */
    public PagedQuery<T> next() {
        page = Page.of(page.number() + 1, page.size());
        return this;
    }

    /**
     * Internal error code.
     */
    public enum Errors {

        /**
         * Gets the page result error.
         */
        GET_PAGE_RESULT_ERROR;
    }

    /**
     * Create a row count CriteriaQuery from a CriteriaQuery
     *
     * @param em       entity manager
     * @param criteria source criteria
     * @param <T>      the entity type.
     * @return row count CriteriaQuery
     */
    public static <T> CriteriaQuery<Long> createCountCriteria(EntityManager em, CriteriaQuery<T> criteria) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> countCriteria = createCountCriteriaQuery(builder, criteria, false);
        Expression<Long> countExpression;
        if (criteria.isDistinct()) {
            countExpression = builder.countDistinct(findRoot(countCriteria, criteria.getResultType()));
        } else {
            countExpression = builder.count(findRoot(countCriteria, criteria.getResultType()));
        }
        return countCriteria.select(countExpression);
    }

    /**
     * Creates count criteria query base on the {@code from} criteria query..
     *
     * @param builder the criteria builder.
     * @param from    source Criteria.
     * @param fetches copy fetches queries.
     * @return count criteria query.
     */
    public static CriteriaQuery<Long> createCountCriteriaQuery(CriteriaBuilder builder, CriteriaQuery<?> from, boolean fetches) {
        CriteriaQuery<Long> result = builder.createQuery(Long.class);

        AliasCounter counter = new AliasCounter();

        // copy the roots and they joins and fetches
        from.getRoots().forEach(root -> {
            Root<?> dest = result.from(root.getJavaType());
            dest.alias(createAlias(root, counter));
            copyJoins(root, dest, counter);
            if (fetches) {
                copyFetches(root, dest);
            }
        });

        // add the group by
        result.groupBy(from.getGroupList());

        // add the distinct
        result.distinct(from.isDistinct());

        // add the group restriction
        if (from.getGroupRestriction() != null) {
            result.having(from.getGroupRestriction());
        }

        // add the predicate
        Predicate predicate = from.getRestriction();
        if (predicate != null) {
            result.where(predicate);
        }
        return result;
    }

    /**
     * Find the Root with type class on {@link CriteriaQuery} Root Set for the {@code clazz}.
     *
     * @param query criteria query
     * @param clazz root type
     * @param <T> the type of the root class.
     * @return the root of the criteria query or {@code null} if none
     */
    public static <T> Root<T> findRoot(CriteriaQuery<?> query, Class<T> clazz) {
        for (Root<?> r : query.getRoots()) {
            if (clazz.equals(r.getJavaType())) {
                return (Root<T>) r.as(clazz);
            }
        }
        return null;
    }

    /**
     * Copy Joins
     *
     * @param from source Join
     * @param to   destination Join
     */
    public static void copyJoins(From<?, ?> from, From<?, ?> to, AliasCounter counter) {
        from.getJoins().forEach(join -> {
            Join<?, ?> item = to.join(join.getAttribute().getName(), join.getJoinType());
            item.alias(createAlias(join, counter));
            copyJoins(join, item, counter);
        });
    }

    /**
     * Copy Fetches
     *
     * @param from source From
     * @param to   destination From
     */
    public static void copyFetches(From<?, ?> from, From<?, ?> to) {
        from.getFetches().forEach(fetch -> {
            Fetch<?, ?> item = to.fetch(fetch.getAttribute().getName(), fetch.getJoinType());
            copyFetches(fetch, item);
        });
    }

    /**
     * Copy Fetches
     *
     * @param from source From
     * @param to   destination From
     */
    public static void copyFetches(Fetch<?, ?> from, Fetch<?, ?> to) {
        from.getFetches().forEach(fetch -> {
            Fetch<?, ?> item = to.fetch(fetch.getAttribute().getName(), fetch.getJoinType());
            copyFetches(fetch, item);
        });
    }

    /**
     * Gets The result alias, if none set a default one and return it
     *
     * @param selection the selection
     * @return root alias or generated one
     */
    public static <T> String createAlias(Selection<T> selection, AliasCounter counter) {
        String alias = selection.getAlias();
        if (alias == null) {
            alias = counter.next();
            selection.alias(alias);
        }
        return alias;

    }

    /**
     * Criteria query alias copy counter.
     */
    public static class AliasCounter {

        private long index = 0;

        public String next() {
            return "a_" + index++;
        }
    }

    @Override
    public String toString() {
        return "PagedQuery{" +
                "page=" + page +
                '}';
    }
}
