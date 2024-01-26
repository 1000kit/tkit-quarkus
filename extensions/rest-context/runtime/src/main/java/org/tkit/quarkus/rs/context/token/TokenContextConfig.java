package org.tkit.quarkus.rs.context.token;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigMapping(prefix = "tkit.rs.context.token")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface TokenContextConfig {

    /**
     * Enable or disable token parsing.
     */
    @WithName("enabled")
    @WithDefault("true")
    boolean enabled();

    /**
     * Make the token mandatory (not null)
     */
    @WithName("mandatory")
    @WithDefault("false")
    boolean mandatory();

    /**
     * Type of the token for new created token
     */
    @WithName("type")
    @WithDefault("principal-token")
    String type();

    /**
     * Verify token
     */
    @WithName("verify")
    @WithDefault("false")
    boolean verify();

    /**
     * Use token realm for the public key.
     */
    @WithName("public-key-location.enabled")
    @WithDefault("false")
    boolean issuerEnabled();

    /**
     * Public key server suffix
     */
    @WithName("public-key-location.suffix")
    @WithDefault("/protocol/openid-connect/certs")
    String issuerSuffix();

    /**
     * Principal token header parameter.
     */
    @WithName("header-param")
    @WithDefault("apm-principal-token")
    String tokenHeaderParam();

}
