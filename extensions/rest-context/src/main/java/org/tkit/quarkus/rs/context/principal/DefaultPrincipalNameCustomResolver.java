package org.tkit.quarkus.rs.context.principal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.Unremovable;

@Unremovable
@DefaultBean
@ApplicationScoped
public class DefaultPrincipalNameCustomResolver implements PrincipalNameCustomResolver {
    @Override
    public String getPrincipalName(ContainerRequestContext containerRequestContext) {
        return null;
    }
}
