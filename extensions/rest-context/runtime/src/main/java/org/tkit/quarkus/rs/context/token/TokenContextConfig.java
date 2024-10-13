package org.tkit.quarkus.rs.context.token;

import java.util.Map;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigDocFilename("tkit-quarkus-rest-context.adoc")
@ConfigMapping(prefix = "tkit.rs.context")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface TokenContextConfig {

    /**
     * Rest context token configuration.
     */
    @WithName("token")
    TokenConfig token();

    interface TokenConfig {
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

        /**
         * Token oidc configuration.
         */
        @WithName("issuers")
        Map<String, IssuerConfig> issuers();

        /**
         * Throw Unauthorized exception for any parser error. Return StatusCode 401.
         */
        @WithName("parser-error-unauthorized")
        @WithDefault("false")
        boolean parserErrorUnauthorized();

        /**
         * Throw Unauthorized exception for required error. Return StatusCode 401.
         */
        @WithName("required-error-unauthorized")
        @WithDefault("false")
        boolean requiredErrorUnauthorized();

        /**
         * Throw Unauthorized exception if access token issuer does not equal to principal token issuer.
         * Return StatusCode 401.
         */
        @WithName("check-tokens-issuer-error-unauthorized")
        @WithDefault("true")
        boolean checkTokensIssuerErrorUnauthorized();

        /**
         * Compare access token issuer with principal token issuer.
         */
        @WithName("check-tokens-issuer")
        @WithDefault("false")
        boolean checkTokensIssuer();
    }

    /**
     * Token oidc configuration.
     */
    interface IssuerConfig {

        /**
         * Enable or disable oidc token config.
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * Token issuer value
         */
        @WithName("url")
        String url();

        /**
         * Use token realm for the public key.
         */
        @WithName("public-key-location.enabled")
        @WithDefault("true")
        boolean publicKeyLocationEnabled();

        /**
         * Public key server suffix
         */
        @WithName("public-key-location.suffix")
        @WithDefault("/protocol/openid-connect/certs")
        String publicKeyLocationSuffix();
    }
}
