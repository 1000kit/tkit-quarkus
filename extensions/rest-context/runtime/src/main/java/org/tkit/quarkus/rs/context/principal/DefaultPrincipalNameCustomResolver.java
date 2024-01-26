package org.tkit.quarkus.rs.context.principal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.Unremovable;

@Unremovable
@DefaultBean
@ApplicationScoped
public class DefaultPrincipalNameCustomResolver implements PrincipalNameCustomResolver {
    @Override
    public String getPrincipalName(JsonWebToken principalToken, ContainerRequestContext containerRequestContext) {
        return null;
    }
}
