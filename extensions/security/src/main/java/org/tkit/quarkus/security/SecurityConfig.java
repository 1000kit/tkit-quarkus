package org.tkit.quarkus.security;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigDocFilename("tkit-quarkus-security.adoc")
@ConfigMapping(prefix = "tkit.security")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface SecurityConfig {

    /**
     * Disable or enabled authentication
     */
    @WithName("auth.enabled")
    @WithDefault("true")
    boolean enabled();

    /**
     * Security events
     */
    @WithName("Events")
    Events events();

    /**
     * Security events
     */
    interface Events {

        /**
         * Log security events
         */
        @WithName("log")
        @WithDefault("true")
        boolean log();
    }
}
