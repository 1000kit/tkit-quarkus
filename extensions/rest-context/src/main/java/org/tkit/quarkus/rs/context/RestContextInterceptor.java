package org.tkit.quarkus.rs.context;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;
import org.tkit.quarkus.rs.context.principal.RestContextPrincipalResolverService;
import org.tkit.quarkus.rs.context.tenant.RestContextTenantResolverService;
import org.tkit.quarkus.rs.context.token.TokenParserRequest;
import org.tkit.quarkus.rs.context.token.TokenParserService;

import io.quarkus.arc.Arc;

@Provider
@Priority(1)
public class RestContextInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger log = LoggerFactory.getLogger(RestContextInterceptor.class);

    @Inject
    RestContextConfig config;

    @Inject
    RestContextPrincipalResolverService principalResolverService;

    @Inject
    RestContextTenantResolverService tenantResolverService;

    @Inject
    TokenParserService tokenParserService;

    private final RestContextHeaderContainer headerContainer;

    public RestContextInterceptor() {
        headerContainer = Arc.container().instance(RestContextHeaderContainer.class).get();
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
        JsonWebToken principalToken = getRestContextPrincipalToken(requestContext);
        if (principalToken == null && config.tokenMandatory()) {
            throw new PrincipalTokenRequiredException();
        }

        // get principal ID
        String principal = principalResolverService.getPrincipalName(principalToken, requestContext);
        if (principal == null && config.principalMandatory()) {
            throw new PrincipalRequiredException();
        }

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

    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        if (!config.enabled()) {
            return;
        }

        // close application context
        ApplicationContext.close();
    }

    private JsonWebToken getRestContextPrincipalToken(ContainerRequestContext containerRequestContext) {
        var tokenConfig = config.token();
        if (!tokenConfig.enabled()) {
            return null;
        }

        String rawToken = containerRequestContext.getHeaders().getFirst(tokenConfig.tokenHeaderParam());
        TokenParserRequest request = new TokenParserRequest(rawToken)
                .issuerEnabled(tokenConfig.issuerEnabled())
                .type(tokenConfig.type())
                .issuerSuffix(tokenConfig.issuerSuffix());
        return tokenParserService.parseToken(request);
    }

}
