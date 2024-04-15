package org.tkit.quarkus.jpa.test;

import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@DisplayName("User DAO tests")
public class UserDAOLikeTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(UserDAOLikeTest.class);

    @Inject
    UserDAO userDAO;

    private void testSearchUser(String input, List<String> output) {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setName(input);

        var pages = userDAO.pageUsers2(criteria, Page.of(0, 10));

        Assertions.assertNotNull(pages);
        PageResult<User> page = pages.getPageResult();
        Assertions.assertNotNull(page);
        var names = page.getStream().map(User::getName).toList();
        log.info("Result: {}", names);
        Assertions.assertTrue(output.containsAll(names));
    }

    @Test
    public void searchUserTest() {
        User c = new User();
        c.setEmail("test@test.test");
        c.setName("rest1");
        userDAO.create(c);

        c = new User();
        c.setEmail("test@test.test");
        c.setName("rest\\name");
        userDAO.create(c);

        c = new User();
        c.setEmail("test@test.test");
        c.setName("rest_name");
        userDAO.create(c);

        testSearchUser("rest_name", List.of("rest_name"));
        testSearchUser("rest_na*", List.of("rest_name"));
        testSearchUser("rest*", List.of("rest1", "rest\\name", "rest_name"));
        testSearchUser("rest\\na*", List.of("rest\\name"));
        testSearchUser("rest\\name", List.of("rest\\name"));
    }

}
