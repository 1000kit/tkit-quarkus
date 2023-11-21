package org.tkit.quarkus.rs.context;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

@RequestScoped
public class RestContextHeaderContainer {

    private static final MultivaluedHashMap<String, String> EMPTY_MAP = new MultivaluedHashMap<>();
    private ContainerRequestContext requestContext;

    void setContainerRequestContext(ContainerRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    public MultivaluedMap<String, String> getHeaders() {
        if (requestContext == null) {
            return EMPTY_MAP;
        } else {
            return requestContext.getHeaders();
        }
    }
}
