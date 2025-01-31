package org.tkit.quarkus.oidc.health;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigDocFilename("tkit-quarkus-oidc-health.adoc")
@ConfigMapping(prefix = "tkit.oidc.health")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface OidcHealthCheckConfig {

    /**
     * Enable or disable oidc health check.
     */
    @WithName("enabled")
    @WithDefault("true")
    boolean enabled();

    /**
     * Oidc default tenant health check.
     */
    @WithName("default")
    DefaultTenantConfig defaultTenant();

    /**
     * Oidc static tenant health check.
     */
    @WithName("static")
    StaticTenantConfig staticTenant();

    /**
     * Oidc default tenant health check.
     */
    interface DefaultTenantConfig {

        /**
         * Enable or disable default tenant oidc health check.
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();
    }

    /**
     * Oidc static tenant health check.
     */
    interface StaticTenantConfig {

        /**
         * Enable or disable static tenant oidc health check.
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();
    }

}
