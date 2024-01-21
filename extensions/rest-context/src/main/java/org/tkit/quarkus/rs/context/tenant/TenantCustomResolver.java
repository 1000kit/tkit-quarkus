package org.tkit.quarkus.rs.context.tenant;

import jakarta.ws.rs.container.ContainerRequestContext;

import org.eclipse.microprofile.jwt.JsonWebToken;

public interface TenantCustomResolver {

    String getTenantId(JsonWebToken principalToken, ContainerRequestContext containerRequestContext);
}
