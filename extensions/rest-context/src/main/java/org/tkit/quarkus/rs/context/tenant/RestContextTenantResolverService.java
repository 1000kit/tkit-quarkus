package org.tkit.quarkus.rs.context.tenant;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwx.JsonWebStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.rs.context.RestContextException;

@RequestScoped
public class RestContextTenantResolverService {

    private static final Logger log = LoggerFactory.getLogger(RestContextTenantResolverService.class);

    @Inject
    RestContextTenantIdConfig config;

    @Inject
    RestCustomTenantResolver customTenantResolver;

    public String getTenantId(ContainerRequestContext containerRequestContext) {
        if (!config.enabled()) {
            return null;
        }

        // check mock service
        if (config.mock().enabled()) {
            return getMockTenantId(containerRequestContext);
        }

        // get tenant-id from custom service
        try {

            String tenantId = customTenantResolver.getTenantId(containerRequestContext);
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

    private String getMockTenantId(ContainerRequestContext containerRequestContext) {

        var mockConfig = config.mock();
        String token = containerRequestContext.getHeaders().getFirst(mockConfig.tokenHeaderParam());
        if (token == null || token.isBlank()) {
            return mockConfig.defaultTenant();
        }

        String organization;
        try {

            var jws = (JsonWebSignature) JsonWebStructure.fromCompactSerialization(token);
            var jwtClaims = JwtClaims.parse(jws.getUnverifiedPayload());
            organization = jwtClaims.getClaimValueAsString(config.mock().claimOrgId());
        } catch (Exception ex) {
            throw new RestContextException(ErrorKeys.ERROR_MOCK_PARSE_TOKEN, "Error parse token. Error: " + ex.getMessage(),
                    ex);
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

        ERROR_MOCK_PARSE_TOKEN,

        ERROR_CALL_CUSTOM_TENANT_SERVICE;
    }
}
