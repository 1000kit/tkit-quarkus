package org.tkit.quarkus.rs.context.principal;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.tkit.quarkus.rs.context.RestContextException;

@RequestScoped
public class RestContextPrincipalResolverService {

    @Inject
    RestContextPrincipalNameConfig config;

    @jakarta.ws.rs.core.Context
    SecurityContext securityContext;

    @Inject
    PrincipalNameCustomResolver customResolver;

    public String getPrincipalName(JsonWebToken principalToken, ContainerRequestContext containerRequestContext) {
        if (!config.enabled()) {
            return null;
        }

        var principal = getPrincipal(principalToken, containerRequestContext);
        if (principal == null && config.mandatory()) {
            throw new PrincipalRequiredException();
        }
        return principal;
    }

    public String getPrincipal(JsonWebToken principalToken, ContainerRequestContext containerRequestContext) {

        // get principal name from custom service
        if (config.enabledCustomService()) {
            try {
                String principalName = customResolver.getPrincipalName(principalToken, containerRequestContext);
                if (principalName != null && !principalName.isBlank()) {
                    return principalName;
                }
            } catch (Exception ex) {
                throw new RestContextException(ErrorKeys.ERROR_CALL_CUSTOM_PRINCIPAL_NAME_SERVICE,
                        "Failed to call custom principal name resolver service, error: " + ex.getMessage(), ex);
            }
        }

        // get principal name from the security context
        if (config.securityContext().enabled() && securityContext != null
                && securityContext.getUserPrincipal() != null) {
            String principal = securityContext.getUserPrincipal().getName();
            if (principal != null && !principal.isBlank()) {
                return principal;
            }
        }

        // get the principal name from the token
        if (config.tokenEnabled()) {
            // check principal name from token
            if (principalToken != null) {
                String principal = principalToken.getClaim(config.tokenClaimName());
                if (principal != null && !principal.isBlank()) {
                    return principal;
                }
            }
        }

        // check principal name from header parameter
        if (config.headerParamEnabled()) {
            String tenantId = containerRequestContext.getHeaders().getFirst(config.headerParamName());
            if (tenantId != null && !tenantId.isBlank()) {
                return tenantId;
            }
        }

        return config.defaultPrincipal().orElse(null);
    }

    public enum ErrorKeys {

        ERROR_CALL_CUSTOM_PRINCIPAL_NAME_SERVICE;
    }
}
