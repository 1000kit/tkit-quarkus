package org.tkit.quarkus.rs.context.principal;

import java.util.Optional;

import io.quarkus.runtime.annotations.*;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigMapping(prefix = "tkit.rs.context.principal.name")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface RestContextPrincipalNameConfig {

    /**
     * Enable or disable principal name resolver.
     */
    @WithName("enabled")
    @WithDefault("true")
    boolean enabled();

    /**
     * Make the principal name mandatory (not null).
     */
    @WithName("mandatory")
    @WithDefault("false")
    boolean mandatory();

    /**
     * Enabled custom service.
     */
    @WithName("custom-service-enabled")
    @WithDefault("false")
    boolean enabledCustomService();

    /**
     * Enable security context for principal name resolver.
     */
    @WithName("security-context")
    SecurityContextConfig securityContext();

    /**
     * Default principal.
     */
    @WithName("default")
    Optional<String> defaultPrincipal();

    /**
     * Enable principal from token.
     */
    @WithName("token-enabled")
    @WithDefault("true")
    boolean tokenEnabled();

    /**
     * Principal from token claim.
     */
    @WithName("token-claim-name")
    @WithDefault("sub")
    String tokenClaimName();

    /**
     * Enable principal from header parameter
     */
    @WithName("header-param-enabled")
    @WithDefault("false")
    boolean headerParamEnabled();

    /**
     * Principal from header parameter
     */
    @WithName("header-param-name")
    @WithDefault("x-principal-id")
    String headerParamName();

    interface SecurityContextConfig {

        /**
         * Enable or disable principal from security context.
         */
        @WithName("enabled")
        @WithDefault("false")
        boolean enabled();
    }
}
