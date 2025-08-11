package org.tkit.quarkus.rs.filter;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;
import org.tkit.quarkus.rs.RestConfig;

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class HeaderResponsePropagationFilter implements ContainerResponseFilter {

    private static final Logger log = Logger.getLogger(HeaderResponsePropagationFilter.class);

    @Inject
    Instance<RestConfig> config;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (config.get().responsePropagationHeaders() == null ||
                config.get().responsePropagationHeaders().isEmpty()) {
            return;
        }
        for (String header : config.get().responsePropagationHeaders()) {
            try {
                String value = requestContext.getHeaderString(header);
                if (value != null) {
                    responseContext.getHeaders().add(header, value);
                }
            } catch (Exception e) {
                log.warn("Error while propagating header from request to response {}", header, e);
            }
        }
    }
}
