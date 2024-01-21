package org.tkit.quarkus.rs.context.principal;

import jakarta.ws.rs.container.ContainerRequestContext;

public interface PrincipalNameCustomResolver {

    String getPrincipalName(ContainerRequestContext containerRequestContext);
}
