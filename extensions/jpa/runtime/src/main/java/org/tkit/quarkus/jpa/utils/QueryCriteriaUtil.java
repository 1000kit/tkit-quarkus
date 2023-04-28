/*
 * Copyright 2019 1000kit.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tkit.quarkus.jpa.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

/**
 * Query criteria Utility class.
 *
 */
public class QueryCriteriaUtil {

    /**
     * The default constructor.
     */
    private QueryCriteriaUtil() {
        // empty constructor
    }

    /**
     * Wildcard the search string with case insensitive {@code true}.
     *
     * @param searchString the search string.
     * @return the corresponding search string.
     * @see QueryCriteriaUtil#wildcard(java.lang.String, boolean)
     */
    public static String wildcard(final String searchString) {
        return wildcard(searchString, true);
    }

    /**
     * Wildcard the search string. Replace * to % and ? to _
     *
     * @param searchString the search string.
     * @param caseInsensitive the case-insensitive flag.
     * @return the corresponding search string.
     */
    public static String wildcard(final String searchString, final boolean caseInsensitive) {
        String result = searchString;
        if (caseInsensitive) {
            result = result.toLowerCase();
        }
        if (searchString.indexOf('*') != -1) {
            result = result.replace('*', '%');
        }
        if (searchString.indexOf('?') != -1) {
            result = result.replace('?', '_');
        }
        return result;
    }

    /**
     * Create an IN clause. If the size of the collection exceeds 1000 items, multiple predicates are created and combined with
     * OR.
     *
     * @param path the path of the parameter
     * @param values the values for the IN clause
     * @param cb the criteria builder for the OR sub-query
     * @return the predicate with the IN clause
     */
    public static Predicate inClause(Expression<?> path, Collection<?> values, CriteriaBuilder cb) {
        Predicate result = path.in(values);
        if (values.size() > 1000) {
            List<Predicate> predicates = new ArrayList<>();
            List<?> valuesList = new ArrayList<>(values);
            while (valuesList.size() > 1000) {
                List<?> subList = valuesList.subList(0, 1000);
                predicates.add(path.in(subList));
                subList.clear();
            }
            predicates.add(path.in(valuesList));
            result = cb.or(predicates.toArray(new Predicate[predicates.size()]));
        }
        return result;
    }

    /**
     * Create a NOT IN clause. If the size of the collection exceeds 1000 items, multiple predicates are created and combined
     * with AND.
     *
     * @param path the path of the parameter
     * @param values the values for the NOT IN clause
     * @param cb the criteria builder for the AND sub-query
     * @return the predicate with the NOT IN clause
     */
    public static Predicate notInClause(Expression<?> path, Collection<?> values, CriteriaBuilder cb) {
        Predicate result = cb.not(path.in(values));
        if (values.size() > 1000) {
            List<Predicate> predicates = new ArrayList<>();
            List<?> valuesList = new ArrayList<>(values);
            while (valuesList.size() > 1000) {
                List<?> subList = valuesList.subList(0, 1000);
                predicates.add(cb.not(path.in(subList)));
                subList.clear();
            }
            predicates.add(cb.not(path.in(valuesList)));
            result = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
        return result;
    }

    /**
     * Create an IN clause in JPQL. If the size of the collection exceeds 1000 items, multiple queries are created and combined
     * with OR.
     *
     * @param attribute the JPQL attribute
     * @param attributeName the attribute name for the parameter replacement
     * @param values the values for the IN clause
     * @param parameters the parameters to be added from the IN clause
     * @return the query string with the IN clause
     */
    public static String inClause(String attribute, String attributeName, Collection<?> values,
            Map<String, Object> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(attribute).append(" IN (:").append(attributeName).append(")");
        List<?> valuesList = new ArrayList<>(values);
        if (values.size() > 1000) {
            int i = 0;
            while (valuesList.size() > 1000) {
                List<?> subList = valuesList.subList(0, 1000);
                sb.append(" OR ").append(attribute).append(" IN (:").append(attributeName).append(i).append(")");
                parameters.put(attributeName + i, new ArrayList<>(subList));
                subList.clear();
                i++;
            }
        }
        sb.append(")");
        parameters.put(attributeName, valuesList);
        return sb.toString();
    }

    /**
     * Create a NOT IN clause in JPQL. If the size of the collection exceeds 1000 items, multiple queries are created and
     * combined with AND.
     *
     * @param attribute the JPQL attribute
     * @param attributeName the attribute name for the parameter replacement
     * @param values the values for the NOT IN clause
     * @param parameters the parameters to be added from the NOT IN clause
     * @return the query string with the NOT IN clause
     */
    public static String notInClause(String attribute, String attributeName, Collection<?> values,
            Map<String, Object> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(attribute).append(" NOT IN (:").append(attributeName).append(")");
        List<?> valuesList = new ArrayList<>(values);
        if (values.size() > 1000) {
            int i = 0;
            while (valuesList.size() > 1000) {
                List<?> subList = valuesList.subList(0, 1000);
                sb.append(" AND ").append(attribute).append(" NOT IN (:").append(attributeName).append(i).append(")");
                parameters.put(attributeName + i, new ArrayList<>(subList));
                subList.clear();
                i++;
            }
        }
        sb.append(")");
        parameters.put(attributeName, valuesList);
        return sb.toString();
    }
}
