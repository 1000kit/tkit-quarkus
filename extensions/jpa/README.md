# tkit-quarkus-jpa

1000kit Quarkus JPA extension

Maven dependency
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-jpa</artifactId>
</dependency>
```
> Note: Consider using official Quarkus Panache extension instead.
> 
## Documentation

The main class of this extension is `AbstractDAO<T>` class which implements basic `CRUD` operation.
The `AbstractDAO<T>` represent `DAO pattern` which implements `CRUD` operation.

```java
@ApplicationScoped
public class UserDAO extends AbstractDAO<User> {
    
}
```
The operation `create`,`delete`,`update` and `findById` are implemented in the abstract class.
In your `DAO` class you need to implement only the business logic.

## Exception

All method of the `AbstractDAO<T>` class throws `DAOException` which is `RuntimeException` and has enumerated `ErrorCode`.
These errors are defined in the `AbstractDAO` class.

The `ConstraintException` extends from the `DAOException` and is use for database constraints.
For example the `create` or `update` operation can throw this exception.

## PageQuery

The `AbstractDAO` class implements the `PageQuery`. With the method `PagedQuery<T> createPageQuery(CriteriaQuery<T> query, Page page)`
could you create a `PageQuery` for you entity. The method `getPageResult` of the `PageQuery` return `PageResult` which contains:
* stream - stream of entities.
* totalElements - total elements in the database for your criteria.
* number - the page number
* size - size of the page
* totalPages - total pages

Examaple method:
```java
public PageResult<User> searchByCriteria(UserSearchCriteria criteria) {
    if (criteria == null) {
        return null;
    }
    CriteriaQuery<User> cq = criteriaQuery();
    Root<User> root = cq.from(User.class);
    List<Predicate> predicates = new ArrayList<>();
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

    if (criteria.getName() != null && !criteria.getName().isEmpty()) {
        predicates.add(cb.like(root.get(User_.USERNAME), wildcard(criteria.getName())));
    }
    if (criteria.getEmail() != null && !criteria.getEmail().isEmpty()) {
        predicates.add(cb.like(root.get(User_.EMAIL), wildcard(criteria.getEmail())));
    }
    if (!predicates.isEmpty()) {
        cq.where(predicates.toArray(new Predicate[0]));
    }

    return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
}
```
Default sorting by id attribute was added to avoid a problem with unpredictable data order for paging.
There could be a situation where some rows are selected from DB more than once, and some rows were skipped. 