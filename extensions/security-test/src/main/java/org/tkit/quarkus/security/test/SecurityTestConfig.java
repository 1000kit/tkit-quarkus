package org.tkit.quarkus.security.test;

import java.util.List;
import java.util.Map;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import io.smallrye.config.WithParentName;

/**
 * Configurations for security tests
 */
@StaticInitSafe
@ConfigDocFilename("tkit-quarkus-security-test.adoc")
@ConfigMapping(prefix = "tkit.security-test")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface SecurityTestConfig {

    /**
     * Disable or enabled security tests
     */
    @WithName("enabled")
    @WithDefault("true")
    boolean enabled();

    /**
     * Definition for multiple operations and assigned options
     */
    @WithParentName
    Map<String, Options> options();

    /**
     * Options for each operation
     */
    interface Options {
        /**
         * Disable or enabled single scope
         */
        @WithName("enabled")
        boolean enabled();

        /**
         * Url used to test the assigned scope
         */
        @WithName("url")
        String url();

        /**
         * Expected status code with valid scope
         */
        @WithName("expectation")
        Integer expectation();

        /**
         * List of client scopes
         */
        @WithName("scopes")
        List<String> scopes();

        /**
         * Used http method to test the given URL
         * valid values: get, post, put, delete
         */
        @WithName("method")
        String method();
    }
}
