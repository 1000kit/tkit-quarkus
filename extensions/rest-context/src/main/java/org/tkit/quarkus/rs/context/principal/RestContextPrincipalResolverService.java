package org.tkit.quarkus.rs.context.principal;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.rs.context.RestContextException;

import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;

@RequestScoped
public class RestContextPrincipalResolverService {

    private static final Logger log = LoggerFactory.getLogger(RestContextPrincipalResolverService.class);

    @Inject
    RestContextPrincipalConfig config;

    @jakarta.ws.rs.core.Context
    SecurityContext securityContext;

    @Inject
    JWTAuthContextInfo authContextInfo;

    @Inject
    JWTParser parser;

    public String getPrincipalName(ContainerRequestContext containerRequestContext) {
        if (!config.enabled()) {
            return null;
        }

        // get principal name from the security context
        if (config.securityContext().enabled() && securityContext != null && securityContext.getUserPrincipal() != null) {
            String principal = securityContext.getUserPrincipal().getName();
            if (principal != null && !principal.isBlank()) {
                return principal;
            }
        }

        // get the principal name from the token
        if (config.token().enabled()) {
            try {
                String principal = getPrincipalNameFromToken(
                        containerRequestContext.getHeaders().getFirst(config.token().tokenHeaderParam()));
                if (principal != null && !principal.isBlank()) {
                    return principal;
                }
            } catch (Exception ex) {
                log.error("Failed to verify/parse a token: {}", config.token().tokenHeaderParam());
                log.error(ex.getMessage(), ex);
                throw new RestContextException(ErrorKeys.ERROR_PARSE_PRINCIPAL_TOKEN,
                        "Failed to verify/parse a token '" + config.token().tokenHeaderParam() + "', error: " + ex.getMessage(),
                        ex);
            }
        }

        return config.defaultPrincipal().orElse(null);
    }

    private String getPrincipalNameFromToken(String token)
            throws InvalidJwtException, JoseException, MalformedClaimException, ParseException {
        if (token == null) {
            return null;
        }

        String principal;

        var jws = (JsonWebSignature) JsonWebStructure.fromCompactSerialization(token);
        var jwtClaims = JwtClaims.parse(jws.getUnverifiedPayload());

        if (config.token().verify()) {
            var info = authContextInfo;

            if (config.token().issuerEnabled()) {
                var publicKeyLocation = jwtClaims.getIssuer() + config.token().issuerSuffix();
                info = new JWTAuthContextInfo(authContextInfo);
                info.setPublicKeyLocation(publicKeyLocation);
            }

            var jwtWebToken = parser.parse(token, info);
            principal = jwtWebToken.getClaim(config.token().claimName());
        } else {
            principal = jwtClaims.getStringClaimValue(config.token().claimName());
        }

        return principal;
    }

    public enum ErrorKeys {

        ERROR_PARSE_PRINCIPAL_TOKEN;
    }
}
