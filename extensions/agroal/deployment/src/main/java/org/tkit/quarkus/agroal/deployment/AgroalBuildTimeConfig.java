package org.tkit.quarkus.agroal.deployment;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigDocFilename("tkit-quarkus-agroal.adoc")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
@ConfigMapping(prefix = "tkit.agroal")
public interface AgroalBuildTimeConfig {

    /**
     * Agroal metrics.
     */
    @WithName("metrics")
    MetricsConfig metrics();

    /**
     * Agroal metrics.
     */
    interface MetricsConfig {

        /**
         * To disable or activate extended agroal metrics.
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();
    }
}
