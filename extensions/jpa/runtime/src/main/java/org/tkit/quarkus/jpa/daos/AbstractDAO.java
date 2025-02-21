package org.tkit.quarkus.jpa.daos;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.AbstractTraceableEntity;

/**
 * The abstract EAO service class using an entity type.
 *
 * @param <T> the entity class {@link AbstractTraceableEntity}.
 */
public abstract class AbstractDAO<T> extends EntityService<T> {

    /**
     * The logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractDAO.class);

    /**
     * The property hint is jakarta.persistence.load-graph.
     * <p>
     * This hint will treat all the specified attributes in the Entity Graph as
     * FetchType.EAGER. Attributes that are not specified are treated as
     * FetchType.LAZY.
     */
    protected static final String HINT_LOAD_GRAPH = "jakarta.persistence.load-graph";

    /**
     * The entity manager.
     */
    @Inject
    protected EntityManager em;

    /**
     * The entity class.
     */
    protected Class<T> entityClass;

    /**
     * The entity name.
     */
    protected String entityName;

    /**
     * The entity name.
     */
    protected String idAttributeName = "id";

    /**
     * Initialize the entity service bean.
     */
    @PostConstruct
    public void init() {
        String serviceClass = getClass().getName();
        entityClass = getEntityClass();
        entityName = getEntityName();
        String tmp = getIdAttributeName();
        if (tmp != null && !tmp.isEmpty()) {
            idAttributeName = tmp;
        }
        log.info("Initialize the entity service {} for entity {}/{}/{}", serviceClass, entityClass, entityName,
                idAttributeName);
    }

    /**
     * Gets the entity manager.
     *
     * @return the entity manager.
     */
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Creates the page query of the DAO {@code <T>} type.
     *
     * @param query the criteria query
     * @param page the page for the query
     * @return the new page query instance
     */
    public PagedQuery<T> createPageQuery(CriteriaQuery<T> query, Page page) {
        return new PagedQuery<>(getEntityManager(), query, page, idAttributeName);
    }

    /**
     * Creates the page query of the DAO {@code <T>} type.
     *
     * @param page the page for the query
     * @return the new page query instance
     */
    public PagedQuery<T> createPageQuery(Page page) {
        CriteriaQuery<T> cq = criteriaQuery();
        cq.from(entityClass);
        return createPageQuery(cq, page);
    }

    /**
     * Creates the page query of the custom {@code <E>} type
     *
     * @param query the criteria query
     * @param page the page for the query
     * @param <E> the entity type of the paged query.
     * @return the new page query instance
     */
    public <E> PagedQuery<E> createPageQueryCustom(CriteriaQuery<E> query, Page page) {
        return new PagedQuery<>(em, query, page, idAttributeName);
    }

    /**
     *
     * /**
     *
     * @deprecated As of version 3.0.0, replaced by {@link #findAllAsList()}
     *             <p>
     *             This method as it returns Stream and before used {@link Query#getResultStream()} which uses server side
     *             cursor,
     *             can produce incorrect data if rows are not explicitly sorted
     *             (@see <a href=
     *             "https://docs.jboss.org/hibernate/orm/6.6/userguide/html_single/Hibernate_User_Guide.html#hql-api-scroll">Related
     *             Hibernate doc</a>)
     *             To avoid this problem it was changed just to wrap {@link #findAllAsList()} and convert it to stream
     *             Use {@link #findAllAsList(EntityGraph)} which is safe without any sorting
     *             <p>
     *             Will be removed in version 3.0.0
     *
     *             Finds all entities.
     *
     * @return the stream of finds entities.
     * @throws DAOException if the method fails.
     */
    @Deprecated(since = "3.0.0", forRemoval = true)
    public Stream<T> findAll() throws DAOException {
        return findAllAsList().stream();
    }

    /**
     * @deprecated As of version 3.0.0, replaced by {@link #findAllAsList(EntityGraph)}
     *             <p>
     *             This method as it returns Stream and before used {@link Query#getResultStream()} which uses server side
     *             cursor,
     *             can produce incorrect data if rows are not explicitly sorted
     *             (@see <a href=
     *             "https://docs.jboss.org/hibernate/orm/6.6/userguide/html_single/Hibernate_User_Guide.html#hql-api-scroll">Related
     *             Hibernate doc</a>).
     *             To avoid this problem it was changed just to wrap {@link #findAllAsList()} and convert it to stream
     *             Use {@link #findAllAsList(EntityGraph)} which is safe without any sorting.
     *             <p>
     *             Will be removed in version 3.0.0
     *             Finds all entities.
     *
     * @param entityGraph the entity graph.
     * @return stream of loaded entities.
     * @throws DAOException if the method fails.
     */
    @Deprecated(since = "3.0.0", forRemoval = true)
    public Stream<T> findAll(EntityGraph<?> entityGraph) throws DAOException {
        return findAllAsList(entityGraph).stream();
    }

    /**
     * Finds all entities
     *
     * @return the list of loaded entities.
     * @throws DAOException if the method fails.
     */
    public List<T> findAllAsList() throws DAOException {
        return findAllAsList(null);
    }

    /**
     * Finds all entities.
     *
     * @param entityGraph the entity graph.
     * @return the list of loaded entities.
     * @throws DAOException if the method fails.
     */

    public List<T> findAllAsList(EntityGraph<?> entityGraph) throws DAOException {
        try {
            CriteriaQuery<T> cq = criteriaQuery();
            cq.from(entityClass);
            cq.distinct(true);
            TypedQuery<T> query = getEntityManager().createQuery(cq);
            if (entityGraph != null) {
                query.setHint(HINT_LOAD_GRAPH, entityGraph);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException(Errors.FIND_ALL_ENTITIES_FAILED, e, entityName,
                    entityGraph == null ? null : entityGraph.getName());
        }
    }

    /**
     * Get an instance, whose state may be lazily fetched. If the requested instance does not exist in the database,
     * the {@code null} is return. The application should not expect that the instance state will be available
     * upon detachment, unless it was accessed by the application while the entity manager was open.
     *
     * @param id the entity ID.
     * @return the entity corresponding to the ID.
     * @throws DAOException if the method fails.
     */
    public T getReference(final Object id) throws DAOException {
        try {
            return getEntityManager().getReference(entityClass, id);
        } catch (EntityNotFoundException n) {
            return null;
        } catch (Exception ex) {
            throw new DAOException(Errors.REFERENCE_ENTITY_BY_ID_FAILED, ex, entityName, id);
        }
    }

    /**
     * Finds the entity by ID.
     *
     * @param id the entity ID.
     * @return the entity corresponding to the ID.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.SUPPORTS, rollbackOn = DAOException.class)
    public T findById(final Object id) throws DAOException {
        try {
            return getEntityManager().find(entityClass, id);
        } catch (Exception e) {
            throw new DAOException(Errors.FIND_ENTITY_BY_ID_FAILED, e, entityName, id);
        }
    }

    /**
     * Finds the entity by ID and entity graph name.
     *
     * @param id the ID.
     * @param entityGraph the entity graph.
     * @return the entity.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.SUPPORTS, rollbackOn = DAOException.class)
    public T findById(Object id, EntityGraph<?> entityGraph) throws DAOException {
        try {
            return getEntityManager().find(entityClass, id, Collections.singletonMap(HINT_LOAD_GRAPH, entityGraph));
        } catch (Exception e) {
            throw new DAOException(Errors.FIND_ENTITY_BY_ID_FAILED, e, entityName, id,
                    entityGraph == null ? null : entityGraph.getName());
        }
    }

    /**
     * Finds the list of object by IDs.
     *
     * @param ids the set of IDs.
     * @return the corresponding list of entities.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.SUPPORTS, rollbackOn = DAOException.class)
    public Stream<T> findByIds(List<Object> ids) throws DAOException {
        return findByIds(ids, null);
    }

    /**
     * Loads all entities.
     *
     * @param ids the set of GUIDs.
     * @param entityGraph the entity graph.
     * @return the list loaded entities.
     * @throws DAOException if the method fails.
     */
    public Stream<T> findByIds(List<Object> ids, EntityGraph<?> entityGraph) throws DAOException {
        try {
            if (ids != null && !ids.isEmpty()) {
                CriteriaQuery<T> cq = criteriaQuery();
                cq.where(cq.from(entityClass).get(idAttributeName).in(ids));
                TypedQuery<T> query = getEntityManager().createQuery(cq);
                if (entityGraph != null) {
                    query.setHint(HINT_LOAD_GRAPH, entityGraph);
                }
                return query.getResultStream();
            }
            return Stream.empty();
        } catch (Exception e) {
            throw new DAOException(Errors.FAILED_TO_GET_ENTITY_BY_IDS, e, entityName,
                    entityGraph == null ? null : entityGraph.getName());
        }
    }

    /**
     * Updates the entity.
     *
     * @param entity the entity.
     * @return the updated entity.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public T update(T entity) throws DAOException {
        try {
            T result = getEntityManager().merge(entity);
            getEntityManager().flush();
            return result;
        } catch (Exception e) {
            throw handleConstraint(e, Errors.MERGE_ENTITY_FAILED);
        }
    }

    /**
     * Updates the entities.
     *
     * @param entities the list of entities.
     * @return the list of updated entities.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public Stream<T> update(Stream<T> entities) throws DAOException {
        if (entities != null) {
            Iterator<T> it = entities.iterator();
            if (it.hasNext()) {
                try {
                    Stream.Builder<T> builder = Stream.builder();
                    it.forEachRemaining(e -> builder.add(getEntityManager().merge(e)));
                    getEntityManager().flush();
                    return builder.build();
                } catch (Exception e) {
                    throw handleConstraint(e, Errors.MERGE_ENTITY_FAILED);
                }
            }
        }
        return Stream.empty();
    }

    /**
     * Updates the entities.
     *
     * @param entities the list of entities.
     * @return the list of updated entities.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public Stream<T> update(List<T> entities) throws DAOException {
        if (entities != null && !entities.isEmpty()) {
            return update(entities.stream());
        }
        return Stream.empty();
    }

    /**
     * Creates the entity.
     *
     * @param entity the entity.
     * @return the created entity.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public T create(T entity) throws DAOException {
        try {
            getEntityManager().persist(entity);
            getEntityManager().flush();
        } catch (Exception e) {
            throw handleConstraint(e, Errors.PERSIST_ENTITY_FAILED);
        }
        return entity;
    }

    /**
     * Creates the entities.
     *
     * @param entities the list of entities.
     * @return list of created entities.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public Stream<T> create(List<T> entities) throws DAOException {
        if (entities != null && !entities.isEmpty()) {
            return create(entities.stream());
        }
        return Stream.empty();
    }

    /**
     * Updates the entities.
     *
     * @param entities the stream of entities.
     * @return the stream of updated entities.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public Stream<T> create(Stream<T> entities) throws DAOException {
        if (entities != null) {
            Iterator<T> it = entities.iterator();
            if (it.hasNext()) {
                try {
                    Stream.Builder<T> result = Stream.builder();
                    it.forEachRemaining(e -> {
                        getEntityManager().persist(e);
                        result.add(e);
                    });
                    getEntityManager().flush();
                    return result.build();
                } catch (Exception e) {
                    throw handleConstraint(e, Errors.PERSIST_ENTITY_FAILED);
                }
            }
        }
        return Stream.empty();
    }

    /**
     * Deletes the entity.
     *
     * @param entity the entity.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public void delete(T entity) throws DAOException {
        try {
            getEntityManager().remove(entity);
            getEntityManager().flush();
        } catch (Exception e) {
            throw handleConstraint(e, Errors.DELETE_ENTITY_FAILED);
        }
    }

    /**
     * Performs delete operation on a list of entities. false is returned if one
     * object fails to be deleted.
     *
     * @param entities the list of entities.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public void delete(List<T> entities) throws DAOException {
        if (entities != null && !entities.isEmpty()) {
            delete(entities.stream());
        }
    }

    /**
     * Performs delete operation on a list of entities. false is returned if one
     * object fails to be deleted.
     *
     * @param entities the list of entities.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public void delete(Stream<T> entities) throws DAOException {
        if (entities != null) {
            Iterator<T> it = entities.iterator();
            if (it.hasNext()) {
                it.forEachRemaining(getEntityManager()::remove);
                getEntityManager().flush();
            }
        }
    }

    /**
     * Performs delete operation on a list of entities. false is returned if one
     * object fails to be deleted.
     *
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public void deleteAll() throws DAOException {
        try {
            List<T> tmp = findAllAsList();
            delete(tmp);
        } catch (Exception e) {
            throw new DAOException(Errors.FAILED_TO_DELETE_ALL, e, entityName);
        }
    }

    /**
     * Removes all entities. Check on existence is made.
     *
     * @return the number of deleted entities.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public int deleteQueryAll() throws DAOException {
        try {
            CriteriaDelete<T> cq = deleteQuery();
            cq.from(entityClass);
            int result = getEntityManager().createQuery(cq).executeUpdate();
            getEntityManager().flush();
            return result;
        } catch (Exception e) {
            throw handleConstraint(e, Errors.FAILED_TO_DELETE_ALL_QUERY);
        }
    }

    /**
     * Removes an entity by GUID. Check on existence is made.
     *
     * @param id the GUID of the entity
     * @return true if removed.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public boolean deleteQueryById(Object id) throws DAOException {
        if (id != null) {
            try {
                CriteriaDelete<T> cq = deleteQuery();
                cq.where(
                        getEntityManager().getCriteriaBuilder()
                                .equal(cq.from(entityClass).get(idAttributeName), id));
                int count = getEntityManager().createQuery(cq).executeUpdate();
                getEntityManager().flush();
                return count == 1;
            } catch (Exception e) {
                throw handleConstraint(e, Errors.FAILED_TO_DELETE_BY_GUID_QUERY);
            }
        }
        return false;
    }

    /**
     * Removes entities by GUIDs. Check on existence is made.
     *
     * @param ids the set of GUIDs.
     * @return the number of deleted entities.
     * @throws DAOException if the method fails.
     */
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public int deleteQueryByIds(List<Object> ids) throws DAOException {
        try {
            if (ids != null && !ids.isEmpty()) {
                CriteriaDelete<T> cq = deleteQuery();
                cq.where(cq.from(entityClass).get(idAttributeName).in(ids));
                int result = getEntityManager().createQuery(cq).executeUpdate();
                getEntityManager().flush();
                return result;
            }
        } catch (Exception e) {
            throw handleConstraint(e, Errors.FAILED_TO_DELETE_ALL_BY_IDS_QUERY);
        }
        return 0;
    }

    /**
     * Creates the named query.
     *
     * @param namedQuery the named query.
     * @param parameters the map of parameters.
     * @return the query.
     */
    protected Query createNamedQuery(String namedQuery, Map<String, Object> parameters) {
        Query query = getEntityManager().createNamedQuery(namedQuery);
        if (parameters != null) {
            parameters.forEach(query::setParameter);
        }
        return query;
    }

    /**
     * Performs persist followed by flush.
     *
     * @param entity the entity.
     */
    protected void refresh(T entity) {
        getEntityManager().refresh(entity);
    }

    /**
     * Lock Entity in EntityManager.
     *
     * @param entity the entity
     * @param lockMode the lock mode
     */
    protected void lock(T entity, LockModeType lockMode) {
        getEntityManager().lock(entity, lockMode);
    }

    /**
     * Handle the JPA constraint exception.
     *
     * @param ex the exception.
     * @param key the error key.
     * @return the corresponding service exception.
     */
    @SuppressWarnings("squid:S1872")
    protected RuntimeException handleConstraint(Exception ex, Enum<?> key) {
        if (ex instanceof ConstraintException ce) {
            return ce;
        }
        if (ex instanceof OptimisticLockException ole) {
            return ole;
        }
        if (ex instanceof ConstraintViolationException cve) {
            var msg = cve.getErrorMessage();
            if (msg != null) {
                msg = msg.replaceAll("\n", "").replaceAll("\"", "'");
            }
            var re = new ConstraintException(msg, key, ex, entityName);
            re.addConstraintName(cve.getConstraintName());
            return re;
        }
        return new DAOException(key, ex, entityName);
    }

    /**
     * Creates the create criteria query.
     *
     * @return the criteria query.
     */
    protected CriteriaQuery<T> criteriaQuery() {
        return this.getEntityManager().getCriteriaBuilder().createQuery(this.entityClass);
    }

    /**
     * Creates the create delete query.
     *
     * @return the delete query.
     */
    protected CriteriaDelete<T> deleteQuery() {
        return getEntityManager().getCriteriaBuilder().createCriteriaDelete(entityClass);
    }

    /**
     * Creates the create update query.
     *
     * @return the update query.
     */
    protected CriteriaUpdate<T> updateQuery() {
        return getEntityManager().getCriteriaBuilder().createCriteriaUpdate(entityClass);
    }

    /**
     * /**
     * The error keys.
     */
    protected enum Errors {
        FAILED_TO_GET_ENTITY_BY_IDS,
        FAILED_TO_DELETE_ALL,
        FAILED_TO_DELETE_ALL_QUERY,
        FAILED_TO_DELETE_BY_GUID_QUERY,
        FAILED_TO_DELETE_ALL_BY_IDS_QUERY,
        PERSIST_ENTITY_FAILED,
        MERGE_ENTITY_FAILED,
        DELETE_ENTITY_FAILED,
        FIND_ENTITY_BY_ID_FAILED,
        FIND_ALL_ENTITIES_FAILED,

        REFERENCE_ENTITY_BY_ID_FAILED,
        ;
    }
}
