package org.tkit.quarkus.jpa.test;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PagedQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

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
            }
        }
        return createPageQuery(cq, page);
    }
}
