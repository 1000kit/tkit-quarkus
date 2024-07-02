package org.tkit.quarkus.security.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configurations for security tests
 */
public class SecurityTestConfig {

    /**
     * Definition for multiple operations and assigned options
     */
    public Map<String, Options> options = new HashMap<>();

    /**
     * Options for each operation
     */
    public static class Options {

        public Options(String url, Integer expectation, List<String> scopes, String method) {
            this.url = url;
            this.expectation = expectation;
            this.scopes = scopes;
            this.method = method;
        }

        /**
         * Url used to test the assigned scope
         */
        String url;

        /**
         * Expected status code with valid scope
         */
        Integer expectation;

        /**
         * List of client scopes
         */
        List<String> scopes;

        /**
         * Used http method to test the given URL
         * valid values: get, post, put, delete
         */
        String method;
    }
}
