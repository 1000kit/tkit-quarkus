package org.tkit.quarkus.rs.context;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;
import org.tkit.quarkus.rs.context.principal.RestContextPrincipalResolverService;
import org.tkit.quarkus.rs.context.tenant.RestContextTenantResolverService;

import io.quarkus.arc.Arc;

@Provider
@Priority(1)
public class RestContextInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    @jakarta.ws.rs.core.Context
    SecurityContext securityContext;

    @Inject
    RestContextConfig config;

    @Inject
    RestContextPrincipalResolverService principalResolverService;

    @Inject
    RestContextTenantResolverService tenantResolverService;

    private final RestContextHeaderContainer headerContainer;

    public RestContextInterceptor() {
        headerContainer = Arc.container().instance(RestContextHeaderContainer.class).get();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if (headerContainer != null) {
            headerContainer.setContainerRequestContext(requestContext);
        }

        // start log scope/correlation ID
        String correlationId = null;
        if (config.correlationId().enabled()) {
            correlationId = requestContext.getHeaders().getFirst(config.correlationId().headerParamName());
        }

        // get business context
        String businessContext = null;
        if (config.businessParam().enabled()) {
            businessContext = requestContext.getHeaders().getFirst(config.businessParam().headerParamName());
            if (businessContext == null || businessContext.isBlank()) {
                businessContext = config.businessParam().defaultBusinessParam().orElse(null);
            }
        }

        // get principal ID
        String principal = principalResolverService.getPrincipalName(requestContext);

        // get tenant ID
        String tenantId = tenantResolverService.getTenantId(requestContext);

        // create a application context
        Context ctx = Context.builder()
                .correlationId(correlationId)
                .principal(principal)
                .tenantId(tenantId)
                .businessContext(businessContext)
                .build();

        // start application context
        ApplicationContext.start(ctx);

    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        // close application context
        ApplicationContext.close();
    }
}
