package org.tkit.quarkus.jpa.test;

import static org.tkit.quarkus.jpa.utils.QueryCriteriaUtil.addSearchStringPredicate;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.*;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PagedQuery;

@ApplicationScoped
public class UserDAO extends AbstractDAO<User> {

    public PagedQuery<User> pageUsersAndSortByName(Page page) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> cq = criteriaQuery();
        Root<User> root = cq.from(User.class);
        cq.orderBy(cb.asc(root.get(User_.NAME)));
        return createPageQuery(cq, page);
    }

    public PagedQuery<User> pageUsers(UserSearchCriteria criteria, Page page) {
        CriteriaQuery<User> cq = criteriaQuery();
        Root<User> root = cq.from(User.class);
        cq.distinct(false);
        if (criteria != null) {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

            if (criteria.getName() != null && !criteria.getName().isEmpty()) {
                predicates.add(cb.like(root.get(User_.NAME), criteria.getName() + "%"));
            }
            if (criteria.getEmail() != null && !criteria.getEmail().isEmpty()) {
                predicates.add(cb.like(root.get(User_.EMAIL), criteria.getEmail() + "%"));
            }
            if (criteria.getCity() != null && !criteria.getCity().isEmpty()) {
                Join<User, Address> addressJoin = root.join(User_.ADDRESS, JoinType.LEFT);
                predicates.add(cb.equal(addressJoin.get(Address_.CITY), criteria.getCity()));
            }
            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[0]));
                cq.orderBy(cb.asc(root.get(User_.CREATION_DATE)));
            }

        }
        return createPageQuery(cq, page);
    }

    public PagedQuery<User> pageUsers2(UserSearchCriteria criteria, Page page) {
        CriteriaQuery<User> cq = criteriaQuery();
        Root<User> root = cq.from(User.class);

        List<Predicate> predicates = new ArrayList<>();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        addSearchStringPredicate(predicates, cb, root.get(User_.NAME), criteria.getName());
        addSearchStringPredicate(predicates, cb, root.get(User_.EMAIL), criteria.getEmail());
        cq.where(predicates.toArray(new Predicate[0]));

        return createPageQuery(cq, page);
    }
}
