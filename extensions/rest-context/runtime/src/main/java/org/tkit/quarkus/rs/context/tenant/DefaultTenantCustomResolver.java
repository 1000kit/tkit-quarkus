package org.tkit.quarkus.rs.context.tenant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.Unremovable;

@Unremovable
@DefaultBean
@ApplicationScoped
public class DefaultTenantCustomResolver implements TenantCustomResolver {
    @Override
    public String getTenantId(JsonWebToken principalToken, ContainerRequestContext containerRequestContext) {
        return null;
    }
}
