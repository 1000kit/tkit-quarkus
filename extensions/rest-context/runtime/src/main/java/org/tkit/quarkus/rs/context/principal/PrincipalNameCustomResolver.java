package org.tkit.quarkus.rs.context.principal;

import jakarta.ws.rs.container.ContainerRequestContext;

import org.eclipse.microprofile.jwt.JsonWebToken;

public interface PrincipalNameCustomResolver {

    String getPrincipalName(JsonWebToken principalToken, ContainerRequestContext containerRequestContext);
}
