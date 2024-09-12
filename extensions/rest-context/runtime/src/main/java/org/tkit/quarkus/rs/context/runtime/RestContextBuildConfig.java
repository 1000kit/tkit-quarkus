package org.tkit.quarkus.rs.context.runtime;

import io.quarkus.runtime.annotations.*;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import io.smallrye.config.WithParentName;

/**
 * Rest context build configuration.
 */
@ConfigDocFilename("tkit-quarkus-rest-context-build.adoc")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
@ConfigMapping(prefix = "tkit.rs.context.build")
public interface RestContextBuildConfig {

    /**
     * Rest context build configuration
     */
    @WithParentName
    BuildConfig build();

    interface BuildConfig {
        /**
         * Auto-discovery configuration.
         */
        @WithName("tenant")
        TenantBuildTimeConfig tenant();

    }

    /**
     * Build tenant annotation config.
     */
    interface TenantBuildTimeConfig {
        /**
         * Enable @TenantExclude annotation discovery
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();
    }

}
