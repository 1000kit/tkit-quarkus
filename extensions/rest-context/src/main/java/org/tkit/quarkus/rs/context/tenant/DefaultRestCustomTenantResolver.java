package org.tkit.quarkus.rs.context.tenant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.Unremovable;

@Unremovable
@DefaultBean
@ApplicationScoped
public class DefaultRestCustomTenantResolver implements RestCustomTenantResolver {
    @Override
    public String getTenantId(ContainerRequestContext containerRequestContext) {
        return null;
    }
}
