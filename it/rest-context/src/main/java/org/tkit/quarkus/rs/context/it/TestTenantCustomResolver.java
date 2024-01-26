package org.tkit.quarkus.rs.context.it;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.container.ContainerRequestContext;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.rs.context.tenant.TenantCustomResolver;

import io.quarkus.arc.Unremovable;

@Unremovable
@RequestScoped
public class TestTenantCustomResolver implements TenantCustomResolver {

    private static final Logger log = LoggerFactory.getLogger(TestTenantCustomResolver.class);

    @Override
    public String getTenantId(JsonWebToken principalToken, ContainerRequestContext containerRequestContext) {

        var tmp = containerRequestContext.getHeaders().getFirst("test-tenant");
        log.info("----> TENANT RESOLVER <------ Test tenant-id: {}", tmp);
        if (tmp == null) {
            throw new RuntimeException("Missing token for tenant");
        }
        return null;
    }
}
