package org.tkit.quarkus.test;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigDocFilename("tkit-quarkus-test.adoc")
@ConfigMapping(prefix = "tkit.test")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface TestConfig {

    /**
     * Disable or enabled integration test flag
     */
    @WithName("integration-test-enabled")
    @WithDefault("false")
    boolean integrationTestEnabled();

    /**
     * CI pipe configuration.
     */
    @WithName("ci")
    CiConfig ciConfig();

    /**
     * CI pipe configuration.
     */
    interface CiConfig {

        /**
         * Disable or enabled for ci pipeline
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * The name of environment variable to check if we are running in the pipe.
         */
        @WithName("env-name")
        @WithDefault("GITLAB_CI")
        String envName();

    }

    /**
     * Rest assured configuration.
     */
    @WithName("rest-assured")
    RestAssuredConfig restAssuredConfig();

    /**
     * Rest assured configuration.
     */
    interface RestAssuredConfig {

        /**
         * Disable or enabled rest-assured config.
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * Rest assured overwrite URI configuration.
         */
        @WithName("overwrite-uri")
        RestAssuredOverwriteUriConfig uriOverwrite();

    }

    /**
     * Rest assured overwrite URI configuration.
     */
    interface RestAssuredOverwriteUriConfig {
        /**
         * Disable or enabled URI overwrite
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * Overwrite the RestAssured URI with a new value.
         */
        @WithName("uri")
        @WithDefault("http://docker")
        String uri();
    }

}
