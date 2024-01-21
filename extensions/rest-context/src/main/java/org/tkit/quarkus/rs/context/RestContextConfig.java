package org.tkit.quarkus.rs.context;

import java.util.Optional;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigMapping(prefix = "tkit.rs.context")
public interface RestContextConfig {

    @WithName("enabled")
    @WithDefault("true")
    boolean enabled();

    @WithName("correlation-id")
    RestContextCorrelationIdConfig correlationId();

    @WithName("business-context")
    RestContextBusinessConfig businessContext();

    @WithName("token")
    TokenConfig token();

    @WithName("token-context")
    @WithDefault("true")
    boolean tokenContext();

    @WithName("token-mandatory")
    @WithDefault("false")
    boolean tokenMandatory();

    @WithName("principal-mandatory")
    @WithDefault("false")
    boolean principalMandatory();

    interface RestContextCorrelationIdConfig {

        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        @WithName("header-param-name")
        @WithDefault("X-Correlation-ID")
        String headerParamName();
    }

    interface RestContextBusinessConfig {

        @WithName("enabled")
        @WithDefault("false")
        boolean enabled();

        @WithName("default")
        Optional<String> defaultBusinessParam();

        @WithName("header-param-name")
        @WithDefault("business-context")
        String headerParamName();

    }

    interface TokenConfig {

        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        @WithName("type")
        @WithDefault("principal-token")
        String type();

        @WithName("verify")
        @WithDefault("false")
        boolean verify();

        @WithName("public-key-location.enabled")
        @WithDefault("false")
        boolean issuerEnabled();

        @WithName("public-key-location.suffix")
        @WithDefault("/protocol/openid-connect/certs")
        String issuerSuffix();

        @WithName("header-param")
        @WithDefault("apm-principal-token")
        String tokenHeaderParam();

    }

}
