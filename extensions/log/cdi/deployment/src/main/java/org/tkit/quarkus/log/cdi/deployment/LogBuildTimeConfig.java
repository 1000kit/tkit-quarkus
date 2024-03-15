package org.tkit.quarkus.log.cdi.deployment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * Build configuration.
 */
@ConfigRoot(prefix = "tkit", name = "log.cdi", phase = ConfigPhase.BUILD_TIME)
public class LogBuildTimeConfig {

    private static final String AUTO_DISCOVER_ANNO = "jakarta.enterprise.context.ApplicationScoped," +
            "jakarta.enterprise.context.Singleton,jakarta.enterprise.context.RequestScoped";

    /**
     * Auto-discovery configuration.
     *
     * @deprecated
     */
    @ConfigItem(name = "auto-discovery")
    public AutoDiscoverBuildTimeConfig autoDiscover;

    /**
     * Auto discovery configuration
     */
    @ConfigGroup
    public static class AutoDiscoverBuildTimeConfig {
        /**
         * Enable autodiscovery
         *
         * @deprecated
         */
        @ConfigItem(name = "enabled", defaultValue = "false")
        @Deprecated
        public boolean enabled;

        /**
         * Binding includes packages.
         */
        @ConfigItem(name = "packages", defaultValue = "org.tkit")
        @Deprecated
        public List<String> packages;

        /**
         * Specify ignore pattern.
         */
        @ConfigItem(name = "ignore.pattern", defaultValue = "")
        @Deprecated
        public Optional<String> ignorePattern;

        /**
         * The list of beans annotation
         */
        @ConfigItem(name = "bean.annotations", defaultValue = AUTO_DISCOVER_ANNO)
        @Deprecated
        public List<String> annoBeans = new ArrayList<>();

    }

}
