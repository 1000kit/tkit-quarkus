package org.tkit.quarkus.rs.context;

import java.util.Optional;

import io.quarkus.runtime.annotations.*;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigDocFilename("tkit-quarkus-rest-context.adoc")
@ConfigMapping(prefix = "tkit.rs.context")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface RestContextConfig {

    /**
     * Enable or disable rest context.
     */
    @WithName("enabled")
    @WithDefault("true")
    boolean enabled();

    /**
     * Correlation ID config.
     */
    @WithName("correlation-id")
    RestContextCorrelationIdConfig correlationId();

    /**
     * Business context configuration.
     */
    @WithName("business-context")
    RestContextBusinessConfig businessContext();

    /**
     * Add token to application context.
     */
    @WithName("add-token-to-context")
    @WithDefault("true")
    boolean tokenContext();

    /**
     * Correlation ID config.
     */
    interface RestContextCorrelationIdConfig {

        /**
         * Enable or disable correlation ID.
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * Header parameter for correlation ID.
         */
        @WithName("header-param-name")
        @WithDefault("x-correlation-id")
        String headerParamName();
    }

    /**
     * Business context config.
     */
    interface RestContextBusinessConfig {

        /**
         * Enable or disable business context.
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * The default business parameter.
         */
        @WithName("default")
        Optional<String> defaultBusinessParam();

        /**
         * Header parameter for business context.
         */
        @WithName("header-param-name")
        @WithDefault("x-business-context")
        String headerParamName();

    }

}
