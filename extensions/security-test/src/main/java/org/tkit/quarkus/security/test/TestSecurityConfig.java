package org.tkit.quarkus.security.test;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

/**
 * Security test configuration.
 */
@ConfigDocFilename("tkit-quarkus-security-test.adoc")
@ConfigMapping(prefix = "tkit.security-test")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
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
         * This property is used to disable SecurityDynamicTest and must be configured in the maven-surefire-plugin.
         * Value to be evaluated by regex.
         * For example ${project.artifactId}
         */
        @WithName("disable-value")
        Optional<String> disableValue();

        /**
         * This property is used to disable SecurityDynamicTest and must be configured in the maven-surefire-plugin.
         * Regex to evaluate value to disable.
         * For example {@code .*(?<!-bff)$} enable only for project ending with -bff suffix.
         */
        @WithName("disable-regex")
        Optional<String> disableRegex();
    }

}
