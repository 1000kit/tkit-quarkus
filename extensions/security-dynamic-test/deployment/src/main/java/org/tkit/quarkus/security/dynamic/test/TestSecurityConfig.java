package org.tkit.quarkus.security.dynamic.test;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/**
 * Security test configuration.
 */
@ConfigDocFilename("tkit-quarkus-security-dynamic-test.adoc")
@ConfigMapping(prefix = "tkit.security-dynamic-test")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public interface TestSecurityConfig {

    /**
     * Openapi configuration.
     */
    @WithName("openapi")
    OpenApi openApi();

    /**
     * Openapi configuration.
     */
    interface OpenApi {

        /**
         * Enable or disable openapi dynamic security test and integration test.
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * This property is used to disable test SecurityDynamicTest.
         * Value to be evaluated by regex.
         * For example ${project.artifactId}
         */
        @WithName("disable-test-value")
        Optional<String> disableTestValue();

        /**
         * This property is used to disable test SecurityDynamicTest.
         * Regex to evaluate value to disable.
         * For example {@code .*(?<!-bff)$} enable only for project ending with -bff suffix.
         */
        @WithName("disable-test-regex")
        Optional<String> disableTestRegex();

        /**
         * This property is used to enabled or disable integration test SecurityDynamicIT.
         */
        @WithName("enabled-integration-test")
        @WithDefault("true")
        boolean enabledIntegrationTest();

        /**
         * This property is used to disable integration test SecurityDynamicIT
         * Value to be evaluated by regex.
         * For example ${project.artifactId}
         */
        @WithName("disable-integration-test-value")
        Optional<String> disableIntegrationTestValue();

        /**
         * This property is used to disable integration test SecurityDynamicIT
         * Regex to evaluate value to disable.
         * For example {@code .*(?<!-bff)$} enable only for project ending with -bff suffix.
         */
        @WithName("disable-integration-test-regex")
        Optional<String> disableIntegrationTestRegex();
    }

}
