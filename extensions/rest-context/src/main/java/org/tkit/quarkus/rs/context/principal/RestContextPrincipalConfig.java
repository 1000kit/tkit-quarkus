package org.tkit.quarkus.rs.context.principal;

import java.util.Optional;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigMapping(prefix = "tkit.rs.context.principal")
public interface RestContextPrincipalConfig {
    @WithName("enabled")
    @WithDefault("true")
    boolean enabled();

    @WithName("security-context")
    SecurityContextConfig securityContext();

    @WithName("token")
    TokenConfig token();

    @WithName("default")
    Optional<String> defaultPrincipal();

    interface TokenConfig {
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        @WithName("verify")
        @WithDefault("false")
        boolean verify();

        @WithName("public-key-location.enabled")
        @WithDefault("false")
        boolean issuerEnabled();

        @WithName("public-key-location.suffix")
        @WithDefault("/protocol/openid-connect/certs")
        boolean issuerSuffix();

        @WithName("token-header-param")
        @WithDefault("apm-principal-token")
        String tokenHeaderParam();

        @WithName("claim-name")
        @WithDefault("sub")
        String claimName();
    }

    interface SecurityContextConfig {
        @WithName("enabled")
        @WithDefault("false")
        boolean enabled();
    }
}
