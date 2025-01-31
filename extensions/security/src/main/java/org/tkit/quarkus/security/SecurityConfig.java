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
    @WithName("events")
    Events events();

    /**
     * Security events
     */
    interface Events {

        /**
         * Authorization events config.
         */
        @WithName("authorization")
        Authorization authorization();

        /**
         * Authentication events config.
         */
        @WithName("authentication")
        Authentication authentication();

    }

    /**
     * Authorization events config.
     */
    interface Authorization {

        /**
         * Log security events
         */
        @WithName("log")
        @WithDefault("true")
        boolean log();

        /**
         * Message template
         * 1 - request method
         * 2 - request path
         * 3 - principal name
         */
        @WithName("template")
        @WithDefault("Authorization failed, response 403, request %1$s %2$s")
        String template();
    }

    /**
     * Authentication events config.
     */
    interface Authentication {

        /**
         * Log security events
         */
        @WithName("log")
        @WithDefault("true")
        boolean log();

        /**
         * Message template
         * 1 - request method
         * 2 - request path
         */
        @WithName("template")
        @WithDefault("Authentication failed, response 401, request %1$s %2$s")
        String template();
    }
}
