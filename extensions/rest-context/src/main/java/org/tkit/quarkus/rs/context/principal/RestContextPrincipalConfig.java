package org.tkit.quarkus.rs.context.principal;

import java.util.Optional;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigMapping(prefix = "tkit.rs.context.principal")
public interface RestContextPrincipalConfig {

    @WithName("name")
    PrincipalName name();

    interface PrincipalName {

        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        @WithName("custom-service-enabled")
        @WithDefault("false")
        boolean enabledCustomService();

        @WithName("security-context")
        SecurityContextConfig securityContext();

        @WithName("default")
        Optional<String> defaultPrincipal();

        @WithName("token-enabled")
        @WithDefault("true")
        boolean tokenEnabled();

        @WithName("token-claim-name")
        @WithDefault("sub")
        String tokenClaimName();

        @WithName("header-param-enabled")
        @WithDefault("false")
        boolean headerParamEnabled();

        @WithName("header-param-name")
        @WithDefault("x-principal-id")
        String headerParamName();
    }

    interface SecurityContextConfig {
        @WithName("enabled")
        @WithDefault("false")
        boolean enabled();
    }
}
