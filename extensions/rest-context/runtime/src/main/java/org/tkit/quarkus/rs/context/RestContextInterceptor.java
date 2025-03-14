package org.tkit.quarkus.rs.context;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.ApplicationContextContainer;
import org.tkit.quarkus.context.Context;
import org.tkit.quarkus.rs.context.principal.RestContextPrincipalResolverService;
import org.tkit.quarkus.rs.context.tenant.RestContextTenantResolverService;
import org.tkit.quarkus.rs.context.token.TokenContextService;

import io.quarkus.arc.Arc;
import io.quarkus.arc.Unremovable;

@Provider
@Unremovable
@Priority(1)
public class RestContextInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    RestContextConfig config;

    @Inject
    RestContextPrincipalResolverService principalResolverService;

    @Inject
    RestContextTenantResolverService tenantResolverService;

    @Inject
    TokenContextService tokenContextService;

    private final RestContextHeaderContainer headerContainer;

    private final ApplicationContextContainer contextContainer;

    public RestContextInterceptor() {
        headerContainer = Arc.container().instance(RestContextHeaderContainer.class).get();
        contextContainer = Arc.container().instance(ApplicationContextContainer.class).get();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {

        if (!config.enabled()) {
            return;
        }

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
        if (config.businessContext().enabled()) {
            businessContext = requestContext.getHeaders().getFirst(config.businessContext().headerParamName());
            if (businessContext == null || businessContext.isBlank()) {
                businessContext = config.businessContext().defaultBusinessParam().orElse(null);
            }
        }

        // get principal token
        JsonWebToken principalToken = tokenContextService.getRestContextPrincipalToken(requestContext);

        // get principal ID
        String principal = principalResolverService.getPrincipalName(principalToken, requestContext);

        // get tenant ID
        String tenantId = tenantResolverService.getTenantId(principalToken, requestContext);

        // disable or enable token in the context
        if (!config.tokenContext()) {
            principalToken = null;
        }

        // create a application context
        Context ctx = Context.builder()
                .correlationId(correlationId)
                .principal(principal)
                .tenantId(tenantId)
                .principalToken(principalToken)
                .businessContext(businessContext)
                .build();

        // start application context
        ApplicationContext.start(ctx);

        // workaround fallback for OIDC interceptor
        if (contextContainer != null) {
            contextContainer.setContext(ctx);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        if (!config.enabled()) {
            return;
        }

        // close application context
        ApplicationContext.close();
    }

}
