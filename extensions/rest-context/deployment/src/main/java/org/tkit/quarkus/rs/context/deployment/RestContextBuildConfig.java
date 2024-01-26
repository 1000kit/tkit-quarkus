package org.tkit.quarkus.rs.context.deployment;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * Rest context build configuration.
 */
@ConfigRoot(prefix = "tkit", name = "rs.context.build", phase = ConfigPhase.BUILD_TIME)
public class RestContextBuildConfig {

    /**
     * Auto-discovery configuration.
     */
    @ConfigItem(name = "tenant")
    public TenantBuildTimeConfig tenant;

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
