package org.tkit.quarkus.rs.context.principal;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.rs.context.RestContextException;
import org.tkit.quarkus.rs.context.token.TokenParserRequest;
import org.tkit.quarkus.rs.context.token.TokenParserService;

@RequestScoped
public class RestContextPrincipalResolverService {

    private static final Logger log = LoggerFactory.getLogger(RestContextPrincipalResolverService.class);

    @Inject
    RestContextPrincipalConfig config;

    @jakarta.ws.rs.core.Context
    SecurityContext securityContext;

    @Inject
    PrincipalNameCustomResolver customResolver;

    @Inject
    TokenParserService tokenParser;

    public JsonWebToken getPrincipalToken(ContainerRequestContext containerRequestContext) {

        var tokenConfig = config.token();
        if (!tokenConfig.enabled()) {
            return null;
        }
        try {
            return getPrincipalToken(tokenConfig, containerRequestContext);
        } catch (Exception ex) {
            log.error("Failed to verify/parse a token: {}", tokenConfig.tokenHeaderParam());
            throw new RestContextException(ErrorKeys.ERROR_PARSE_PRINCIPAL_TOKEN,
                    "Failed to verify/parse a token '" + tokenConfig.tokenHeaderParam() + "', error: " + ex.getMessage(),
                    ex);
        }
    }

    public String getPrincipalName(JsonWebToken principalToken, ContainerRequestContext containerRequestContext) {

        var principalNameConfig = config.name();
        if (!principalNameConfig.enabled()) {
            return null;
        }

        // get principal name from custom service
        if (principalNameConfig.enabledCustomService()) {
            try {
                String principalName = customResolver.getPrincipalName(containerRequestContext);
                if (principalName != null && !principalName.isBlank()) {
                    return principalName;
                }
            } catch (Exception ex) {
                throw new RestContextException(ErrorKeys.ERROR_CALL_CUSTOM_PRINCIPAL_NAME_SERVICE,
                        "Failed to call custom principal name resolver service, error: " + ex.getMessage(), ex);
            }
        }

        // get principal name from the security context
        if (principalNameConfig.securityContext().enabled() && securityContext != null
                && securityContext.getUserPrincipal() != null) {
            String principal = securityContext.getUserPrincipal().getName();
            if (principal != null && !principal.isBlank()) {
                return principal;
            }
        }

        // get the principal name from the token
        if (principalNameConfig.tokenEnabled()) {
            try {
                // load token if not principal token not active
                if (principalToken == null) {
                    principalToken = getPrincipalToken(config.token(), containerRequestContext);
                }

                // check principal name from token
                if (principalToken != null) {
                    String principal = principalToken.getClaim(principalNameConfig.tokenClaimName());
                    if (principal != null && !principal.isBlank()) {
                        return principal;
                    }
                }
            } catch (Exception ex) {
                log.error("Failed to verify/parse a token: {}", config.token().tokenHeaderParam());
                throw new RestContextException(ErrorKeys.ERROR_PARSE_PRINCIPAL_TOKEN,
                        "Failed to verify/parse a token '" + config.token().tokenHeaderParam() + "', error: " + ex.getMessage(),
                        ex);
            }
        }

        // check tenant-id from header
        if (principalNameConfig.headerParamEnabled()) {
            String tenantId = containerRequestContext.getHeaders().getFirst(principalNameConfig.headerParamName());
            if (tenantId != null && !tenantId.isBlank()) {
                return tenantId;
            }
        }

        return principalNameConfig.defaultPrincipal().orElse(null);
    }

    private JsonWebToken getPrincipalToken(RestContextPrincipalConfig.TokenConfig tokenConfig,
            ContainerRequestContext containerRequestContext) {
        String rawToken = containerRequestContext.getHeaders().getFirst(tokenConfig.tokenHeaderParam());
        TokenParserRequest request = new TokenParserRequest(rawToken)
                .issuerEnabled(tokenConfig.issuerEnabled())
                .type(tokenConfig.type())
                .issuerSuffix(tokenConfig.issuerSuffix());
        return tokenParser.parseToken(request);
    }

    public enum ErrorKeys {

        ERROR_CALL_CUSTOM_PRINCIPAL_NAME_SERVICE,
        ERROR_PARSE_PRINCIPAL_TOKEN;
    }
}
