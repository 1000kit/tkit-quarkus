package org.tkit.quarkus.log.cdi.runtime;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.*;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/**
 * Build configuration.
 */
@ConfigDocFilename("tkit-quarkus-log-cdi.adoc")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
@ConfigMapping(prefix = "tkit.log.cdi.build")
public interface LogBuildTimeConfig {

    String AUTO_DISCOVER_ANNO = "jakarta.enterprise.context.ApplicationScoped," +
            "jakarta.enterprise.context.Singleton,jakarta.enterprise.context.RequestScoped";

    /**
     * Auto-discovery configuration.
     *
     * @deprecated
     */
    @WithName("auto-discovery")
    AutoDiscoverBuildTimeConfig autoDiscover();

    /**
     * Auto discovery configuration
     */
    interface AutoDiscoverBuildTimeConfig {
        /**
         * Enable autodiscovery
         *
         * @deprecated
         */
        @WithName("enabled")
        @WithDefault("false")
        @Deprecated
        boolean enabled();

        /**
         * Binding includes packages.
         */
        @WithName("packages")
        @WithDefault("org.tkit")
        @Deprecated
        List<String> packages();

        /**
         * Specify ignore pattern.
         */
        @WithName("ignore.pattern")
        @WithDefault("")
        @Deprecated
        Optional<String> ignorePattern();

        /**
         * The list of beans annotation
         */
        @WithName("bean.annotations")
        @WithDefault(AUTO_DISCOVER_ANNO)
        @Deprecated
        List<String> annoBeans();

    }

}
