package org.tkit.quarkus.jpa.test;

import io.quarkus.test.junit.QuarkusTest;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.daos.PagedQuery;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.persistence.criteria.Order;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@QuarkusTest
@DisplayName("User DAO tests")
public class UserDAOTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(UserDAOTest.class);

    @Inject
    UserDAO userDAO;

    @Inject
    AddressDAO addressDAO;

    @Test
    public void updateUserTest() {
        User c = new User();
        c.setEmail("Rest@Rest.Rest");
        c.setName("RestName");
        final User user = userDAO.create(c);

        User loaded = userDAO.findById(user.getId());
        loaded.setName("update-name");
        userDAO.update(loaded);

        user.setName("update-name");
        Assertions.assertThrows(DAOException.class, () -> {
            userDAO.update(user);
        });
    }

    @Test
    public void user5000PagingTest() {
        // create 5000 users
        userDAO.create(Stream.generate(UserTestBuilder::createUser).limit(5000));
        List<String> userIds = new ArrayList<>(5000);
        for (int i = 0; i < 10; i++) {
            PagedQuery<User> query = userDAO.createPageQuery(Page.of(i, 500));
            PageResult<User> page = query.getPageResult();
            userIds.addAll(page.getStream().map(TraceableEntity::getId).collect(Collectors.toList()));
        }
        Assertions.assertEquals(5000, userIds.size());
        Assertions.assertEquals(5000, userIds.stream().distinct().count());
    }

    @Test
    public void userDefaultSortingPagingTest(){
        // create 150 users
        userDAO.create(Stream.generate(UserTestBuilder::createUser).limit(150));
        PagedQuery<User> query = userDAO.createPageQuery(Page.of(0, 10));
        PageResult<User> page = query.getPageResult();
        Assertions.assertEquals(1, query.criteria().getOrderList().size());
        Order order = query.criteria().getOrderList().get(0);
        SingularAttributePath<?> expression = (SingularAttributePath<?>) order.getExpression();
        Assertions.assertEquals("id", expression.getAttribute().getName());
        Assertions.assertEquals(10, (int) page.getStream().map(TraceableEntity::getId).count());
    }

    @Test
    public void userSortByNamePagingTest(){
        // create 150 users
        userDAO.create(Stream.generate(UserTestBuilder::createUser).limit(150));
        PagedQuery<User> query = userDAO.pageUsersAndSortByName(Page.of(0, 10));
        PageResult<User> page = query.getPageResult();
        Assertions.assertEquals(1, query.criteria().getOrderList().size());
        Order order = query.criteria().getOrderList().get(0);
        SingularAttributePath<?> expression = (SingularAttributePath<?>) order.getExpression();
        Assertions.assertEquals("name", expression.getAttribute().getName());
        Assertions.assertEquals(10, (int) page.getStream().map(TraceableEntity::getId).count());
    }

    @Test
    public void userPagingTest() {
        // create 150 users
        userDAO.create(Stream.generate(UserTestBuilder::createUser).limit(150));

        PagedQuery<User> query = userDAO.createPageQuery(Page.of(0, 10));

        PageResult<User> page = query.getPageResult();
        log.info("{}", page);
        log.info("{}", page.getStream().map(TraceableEntity::getId).collect(Collectors.toList()));

        query.next().getPageResult();
        page = query.getPageResult();
        log.info("{}", page);
        log.info("{}", page.getStream().map(TraceableEntity::getId).collect(Collectors.toList()));
    }

    @Test
    public void userPagingCriteriaTest() {
        // create 100 users
        Address address = new Address();
        address.setCity("Bratislava");
        address = addressDAO.create(address);

        userDAO.create(UserTestBuilder.createIndexUsers(100, address));

        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setName("Name_1");
        criteria.setEmail("Email_");
        criteria.setCity("Bratislava");
        PagedQuery<User> pages = userDAO.pageUsers(criteria, Page.of(0, 10));

        PageResult<User> page = pages.getPageResult();
        log.info("{}", page);
        log.info("{}", page.getStream().map(User::getName).collect(Collectors.toList()));

        page = pages.next().getPageResult();
        log.info("{}", page);
        log.info("{}", page.getStream().map(User::getName).collect(Collectors.toList()));

        page = pages.next().getPageResult();
        log.info("{}", page);
        log.info("{}", page.getStream().map(User::getName).collect(Collectors.toList()));
    }

    @Test
    public void pageCountSizeTest() {
        // create 150 users
        userDAO.create(Stream.generate(UserTestBuilder::createUser).limit(10));

        PagedQuery<User> query = userDAO.createPageQuery(Page.of(100, 100));

        PageResult<User> page = query.getPageResult();
        log.info("{}", page);
        log.info("{}", page.getStream().map(TraceableEntity::getId).collect(Collectors.toList()));
    }

    @Test
    @Transactional
    public void deleteAllUsersTest() {
        User user = UserTestBuilder.createUser();
        userDAO.create(user);
        User foundUser1 = userDAO.findById(user.getId());
        Assertions.assertNotNull(foundUser1);
        userDAO.deleteAll();
        User foundUser2 = userDAO.findById(user.getId());
        Assertions.assertNull(foundUser2);
    }

    @Test
    public void deleteByIdTest() {
        User user = UserTestBuilder.createUser();
        String userIdForDeleting = "123456789";
        user.setId(userIdForDeleting);
        userDAO.create(user);
        User foundUser1 = userDAO.findById(user.getId());
        Assertions.assertNotNull(foundUser1);
        boolean deleted = userDAO.deleteQueryById(foundUser1.getId());
        Assertions.assertTrue(deleted);
    }

    @Test
    @Transactional
    public void deleteEntityTest() {
        User user = UserTestBuilder.createUser();
        userDAO.create(user);
        User foundUser1 = userDAO.findById(user.getId());
        Assertions.assertNotNull(foundUser1);
        userDAO.delete(foundUser1);
        User foundUser2 = userDAO.findById(user.getId());
        Assertions.assertNull(foundUser2);
    }

    @Test
    public void updateEntitiesTest() {
        User user1 = UserTestBuilder.createUser();
        User user2 = UserTestBuilder.createUser();
        userDAO.create(user1);
        userDAO.create(user2);
        user1.setEmail("email1@test.com");
        user2.setEmail("email2@test.com");
        List<User> users = List.of(user1, user2);
        userDAO.update(users);
        User foundUser1 = userDAO.findById(user1.getId());
        User foundUser2 = userDAO.findById(user2.getId());
        Assertions.assertEquals(foundUser1.getEmail(), user1.getEmail());
        Assertions.assertEquals(foundUser2.getEmail(), user2.getEmail());
    }

    public static class UserTestBuilder {

        public static User createUser() {
            User user = new User();
            user.setName("Name_" + UUID.randomUUID());
            user.setEmail("Email_" + UUID.randomUUID());
            return user;
        }

        public static User createIndexUser(int index) {
            User user = new User();
            user.setName("Name_" + index + "_" + UUID.randomUUID());
            user.setEmail("Email_" + UUID.randomUUID());
            return user;
        }

        public static Stream<User> createIndexUsers(int count, Address address) {
            List<User> tmp = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                User u = createIndexUser(i);
                u.setAddress(address);
                tmp.add(u);
            }
            return tmp.stream();
        }
    }
}
