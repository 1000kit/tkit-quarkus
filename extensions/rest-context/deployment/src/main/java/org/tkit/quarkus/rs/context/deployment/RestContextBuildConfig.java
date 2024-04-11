package org.tkit.quarkus.rs.context.deployment;

import io.quarkus.runtime.annotations.*;

/**
 * Rest context build configuration.
 */
@ConfigDocFilename("tkit-quarkus-rest-context.adoc")
@ConfigRoot(prefix = "tkit", name = "rs.context", phase = ConfigPhase.BUILD_TIME)
public class RestContextBuildConfig {

    /**
     * Rest context build configuration
     */
    @ConfigItem(name = "build")
    public BuildConfig build;

    @ConfigGroup
    public static class BuildConfig {
        /**
         * Auto-discovery configuration.
         */
        @ConfigItem(name = "tenant")
        public TenantBuildTimeConfig tenant;

    }

    /**
     * Build tenant annotation config.
     */
    @ConfigGroup
    public static class TenantBuildTimeConfig {
        /**
         * Enable tenant annotation discovery
         */
        @ConfigItem(name = "enabled", defaultValue = "true")
        public boolean enabled;
    }

}
