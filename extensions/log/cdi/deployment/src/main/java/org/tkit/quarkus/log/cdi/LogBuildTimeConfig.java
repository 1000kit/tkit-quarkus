package org.tkit.quarkus.log.cdi;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * Build configuration.
 */
@ConfigRoot(name = "tkit.log.cdi", phase = ConfigPhase.BUILD_TIME)
public class LogBuildTimeConfig {

    /**
     * Auto-discover configuration.
     */
    @ConfigItem(name = "auto-discover")
    public AutoDiscoverBuildTimeConfig autoDiscover;

    /**
     * Auto discovery configuration
     */
    @ConfigGroup
    public static class AutoDiscoverBuildTimeConfig {
        /**
         * Enable autodiscovery
         */
        @ConfigItem(name = "enabled", defaultValue = "false")
        public boolean enabled;

        /**
         * Binding includes packages.
         */
        @ConfigItem(name = "packages", defaultValue = "org.tkit")
        public List<String> packages;

        /**
         * Specify ignore pattern.
         */
        @ConfigItem(name = "ignore.pattern", defaultValue = "")
        public Optional<String> ignorePattern;
    }

}
