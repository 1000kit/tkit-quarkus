package org.tkit.quarkus.rs.context.tenant;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.rs.context.RestContextException;

@RequestScoped
public class RestContextTenantResolverService {

    private static final Logger log = LoggerFactory.getLogger(RestContextTenantResolverService.class);

    @Inject
    RestContextTenantIdConfig config;

    @Inject
    TenantCustomResolver customTenantResolver;

    public String getTenantId(JsonWebToken principalToken, ContainerRequestContext containerRequestContext) {
        if (!config.enabled()) {
            return null;
        }

        // check mock service
        if (config.mock().enabled()) {
            return getMockTenantId(principalToken);
        }

        if (config.token().enabled()) {
            // check principal name from token
            if (principalToken != null) {
                String tenantId = principalToken.getClaim(config.token().claimTenantParam());
                if (tenantId != null && !tenantId.isBlank()) {
                    return tenantId;
                }
            }
        }

        // get tenant-id from custom service
        try {
            String tenantId = customTenantResolver.getTenantId(principalToken, containerRequestContext);
            if (tenantId != null && !tenantId.isBlank()) {
                return tenantId;
            }
        } catch (Exception ex) {
            log.error("Failed to call custom tenant resolver service, error: " + ex.getMessage(), ex);
            throw new RestContextException(ErrorKeys.ERROR_CALL_CUSTOM_TENANT_SERVICE,
                    "Failed to call custom tenant resolver service, error: " + ex.getMessage(), ex);
        }

        // check tenant-id from header
        if (config.headerParamEnabled()) {
            String tenantId = containerRequestContext.getHeaders().getFirst(config.headerParamName());
            if (tenantId != null && !tenantId.isBlank()) {
                return tenantId;
            }
        }

        return config.defaultTenantId();
    }

    private String getMockTenantId(JsonWebToken principalToken) {

        var mockConfig = config.mock();
        String organization = null;
        if (principalToken != null) {
            organization = principalToken.getClaim(mockConfig.claimOrgId());
        }

        if (organization == null) {
            return mockConfig.defaultTenant();
        }

        var tenantId = mockConfig.data().get(organization);
        if (tenantId == null) {
            return mockConfig.defaultTenant();
        }

        return tenantId;
    }

    public enum ErrorKeys {

        ERROR_CALL_CUSTOM_TENANT_SERVICE;
    }
}
