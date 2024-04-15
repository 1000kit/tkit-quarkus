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
import java.util.function.Function;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

/**
 * Query criteria Utility class.
 *
 */
public class QueryCriteriaUtil {

    /**
     * Custom query like characters.
     */
    private static final Map<String, String> DEFAULT_LIKE_MAPPING_CHARACTERS = Map.of(
            "*", "%",
            "?", "_");

    private static final char DEFAULT_ESCAPE_CHAR = '\\';

    /**
     * The default constructor.
     */
    private QueryCriteriaUtil() {
        // empty constructor
    }

    /**
     * Wildcard the search string with case-insensitive {@code true}.
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
            result = cb.or(predicates.toArray(new Predicate[0]));
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
            result = cb.and(predicates.toArray(new Predicate[0]));
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
    @Deprecated
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
    @Deprecated
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

    /**
     * Add a search predicate to the list of predicates.
     *
     * @param predicates - list of predicates
     * @param criteriaBuilder - CriteriaBuilder
     * @param column - column Path [root.get(Entity_.attribute)]
     * @param searchString - string to search. if Contains [*,?] like will be used
     * @param caseInsensitive - true in case of insensitive search (db column and search string are given to lower case)
     * @return {@code true} add predicate to the list
     */
    public static boolean addSearchStringPredicate(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
            Expression<String> column,
            String searchString, final boolean caseInsensitive) {
        if (predicates == null) {
            return false;
        }
        var predicate = createSearchStringPredicate(criteriaBuilder, column, searchString, caseInsensitive);
        if (predicate == null) {
            return false;
        }

        return predicates.add(predicate);
    }

    /**
     * Add a search predicate as a case of insensitive search to the list of predicates.
     *
     * @param predicates - list of predicates
     * @param criteriaBuilder - CriteriaBuilder
     * @param column - column Path [root.get(Entity_.attribute)]
     * @param searchString - string to search. if Contains [*,?] like will be used
     * @return {@code true} add predicate to the list
     */
    public static boolean addSearchStringPredicate(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
            Expression<String> column,
            String searchString) {
        if (predicates == null) {
            return false;
        }
        var predicate = createSearchStringPredicate(criteriaBuilder, column, searchString, true);
        if (predicate == null) {
            return false;
        }

        return predicates.add(predicate);
    }

    /**
     * Create a search predicate as a case of insensitive search.
     *
     * @param criteriaBuilder - CriteriaBuilder
     * @param column - column Path [root.get(Entity_.attribute)]
     * @param searchString - string to search. if Contains [*,?] like will be used
     * @return LIKE or EQUAL Predicate according to the search string
     */
    public static Predicate createSearchStringPredicate(CriteriaBuilder criteriaBuilder, Expression<String> column,
            String searchString) {
        return createSearchStringPredicate(criteriaBuilder, column, searchString, true);
    }

    /**
     * Create a search predicate.
     *
     * @param criteriaBuilder - CriteriaBuilder
     * @param column - column Path [root.get(Entity_.attribute)]
     * @param searchString - string to search. if Contains [*,?] like will be used
     * @param caseInsensitive - true in case of insensitive search (db column and search string are given to lower case)
     * @return LIKE or EQUAL Predicate according to the search string
     */
    public static Predicate createSearchStringPredicate(CriteriaBuilder criteriaBuilder, Expression<String> column,
            String searchString, final boolean caseInsensitive) {
        return createSearchStringPredicate(criteriaBuilder, column, searchString, caseInsensitive,
                QueryCriteriaUtil::defaultReplaceFunction, DEFAULT_LIKE_MAPPING_CHARACTERS, DEFAULT_ESCAPE_CHAR);
    }

    /**
     * Create a search predicate.
     *
     * @param criteriaBuilder - CriteriaBuilder
     * @param column - column Path [root.get(Entity_.attribute)]
     * @param searchString - string to search. if Contains [*,?] like will be used
     * @param caseInsensitive - true in case of insensitive search (db column and search string are given to lower case)
     * @param replaceFunction - replace special character function for characters in the searchString
     * @param likeMapping - map of like query mapping characters ['*','%', ...]
     * @param escapeChar - escape character for like statement
     * @return LIKE or EQUAL Predicate according to the search string
     */
    public static Predicate createSearchStringPredicate(CriteriaBuilder criteriaBuilder, Expression<String> column,
            String searchString, final boolean caseInsensitive,
            Function<String, String> replaceFunction,
            Map<String, String> likeMapping, char escapeChar) {

        if (searchString == null || searchString.isBlank()) {
            return null;
        }

        // case insensitive
        Expression<String> columnDefinition = column;
        if (caseInsensitive) {
            searchString = searchString.toLowerCase();
            columnDefinition = criteriaBuilder.lower(column);
        }

        // check for like characters
        boolean like = false;
        if (likeMapping != null) {
            for (String item : likeMapping.keySet()) {
                if (searchString.contains(item)) {
                    like = true;
                    break;
                }
            }
        }

        // like predicate
        if (like) {

            // replace function for special characters
            if (replaceFunction != null) {
                searchString = replaceFunction.apply(searchString);
            }

            // replace for like characters
            for (Map.Entry<String, String> item : likeMapping.entrySet()) {
                if (searchString.contains(item.getKey())) {
                    searchString = searchString.replace(item.getKey(), item.getValue());
                }
            }
            return criteriaBuilder.like(columnDefinition, searchString, escapeChar);
        }

        // equal predicate
        return criteriaBuilder.equal(columnDefinition, searchString);
    }

    /**
     * Escape the extra DB characters
     */
    public static String defaultReplaceFunction(String searchString) {
        return searchString
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
