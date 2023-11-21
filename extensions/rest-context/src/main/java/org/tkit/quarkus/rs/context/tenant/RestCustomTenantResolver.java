package org.tkit.quarkus.rs.context.tenant;

import jakarta.ws.rs.container.ContainerRequestContext;

public interface RestCustomTenantResolver {

    String getTenantId(ContainerRequestContext containerRequestContext);
}
