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
    RestContextPrincipalConfig config;

    @jakarta.ws.rs.core.Context
    SecurityContext securityContext;

    @Inject
    PrincipalNameCustomResolver customResolver;

    public String getPrincipalName(JsonWebToken principalToken, ContainerRequestContext containerRequestContext) {

        var principalNameConfig = config.name();
        if (!principalNameConfig.enabled()) {
            return null;
        }

        // get principal name from custom service
        if (principalNameConfig.enabledCustomService()) {
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
        if (principalNameConfig.securityContext().enabled() && securityContext != null
                && securityContext.getUserPrincipal() != null) {
            String principal = securityContext.getUserPrincipal().getName();
            if (principal != null && !principal.isBlank()) {
                return principal;
            }
        }

        // get the principal name from the token
        if (principalNameConfig.tokenEnabled()) {
            // check principal name from token
            if (principalToken != null) {
                String principal = principalToken.getClaim(principalNameConfig.tokenClaimName());
                if (principal != null && !principal.isBlank()) {
                    return principal;
                }
            }
        }

        // check principal name from header parameter
        if (principalNameConfig.headerParamEnabled()) {
            String tenantId = containerRequestContext.getHeaders().getFirst(principalNameConfig.headerParamName());
            if (tenantId != null && !tenantId.isBlank()) {
                return tenantId;
            }
        }

        return principalNameConfig.defaultPrincipal().orElse(null);
    }

    public enum ErrorKeys {

        ERROR_CALL_CUSTOM_PRINCIPAL_NAME_SERVICE;
    }
}
