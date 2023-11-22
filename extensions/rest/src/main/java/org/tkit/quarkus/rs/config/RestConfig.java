package org.tkit.quarkus.rs.config;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "tkit.rs")
@StaticInitSafe
public interface RestConfig {

    /**
     * Log exception stacktrace thrown out of controller
     */
    @WithName("log.stacktrace")
    @WithDefault("false")
    boolean logStacktrace();
}
