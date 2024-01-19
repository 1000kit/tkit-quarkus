package org.tkit.quarkus.jpa.utils;

import static org.mockito.ArgumentMatchers.any;

import jakarta.persistence.criteria.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

class QueryCriteriaUtilTest {

    @Test
    void createSearchStringPredicateTest() {

        final var result = new TestResult();

        Predicate predicate = Mockito.mock(Predicate.class);
        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        Mockito.when(cb.lower(any())).thenReturn(null);
        Mockito.when(cb.like(any(), (String) any())).thenAnswer(invocation -> {
            result.like = true;
            result.searchString = invocation.getArgument(1);
            return predicate;
        });
        Mockito.when(cb.equal(any(), (String) any())).thenAnswer((Answer<Predicate>) invocation -> {
            result.equal = true;
            result.searchString = invocation.getArgument(1);
            return predicate;
        });

        Expression<String> expression = Mockito.mock(Expression.class);
        Mockito.when(cb.lower(any())).thenAnswer(invocation -> {
            result.lower = false;
            return expression;
        });

        Predicate p = QueryCriteriaUtil.createSearchStringPredicate(cb, expression, "searchText", false);
        Assertions.assertNotNull(p);
        Assertions.assertFalse(result.like);
        Assertions.assertTrue(result.equal);
        Assertions.assertEquals("searchText", result.searchString);

        result.reset();
        p = QueryCriteriaUtil.createSearchStringPredicate(cb, expression, "ThisIsNotSearchText", true);
        Assertions.assertNotNull(p);
        Assertions.assertFalse(result.like);
        Assertions.assertTrue(result.equal);
        Assertions.assertEquals("thisisnotsearchtext", result.searchString);

        result.reset();
        p = QueryCriteriaUtil.createSearchStringPredicate(cb, expression, "?ThisIsNotSearchText", true);
        Assertions.assertNotNull(p);
        Assertions.assertFalse(result.equal);
        Assertions.assertTrue(result.like);
        Assertions.assertEquals("_thisisnotsearchtext", result.searchString);

        result.reset();
        p = QueryCriteriaUtil.createSearchStringPredicate(cb, expression, "?ThisIsNotSearchText*", true);
        Assertions.assertNotNull(p);
        Assertions.assertFalse(result.equal);
        Assertions.assertTrue(result.like);
        Assertions.assertEquals("_thisisnotsearchtext%", result.searchString);
    }

    public static class TestResult {
        boolean like = false;
        boolean equal = false;
        boolean lower = false;

        String searchString = null;

        void reset() {
            like = false;
            equal = false;
            lower = false;
            searchString = null;
        }
    }
}
