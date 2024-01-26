package org.tkit.quarkus.rs.context.token;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;

import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class TokenContextService {

    @Inject
    TokenParserService tokenParserService;

    @Inject
    TokenContextConfig config;

    public JsonWebToken getRestContextPrincipalToken(ContainerRequestContext containerRequestContext) {
        if (!config.enabled()) {
            return null;
        }

        var token = getToken(containerRequestContext);
        if (token == null && config.mandatory()) {
            throw new PrincipalTokenRequiredException();
        }
        return token;
    }

    private JsonWebToken getToken(ContainerRequestContext containerRequestContext) {

        String rawToken = containerRequestContext.getHeaders().getFirst(config.tokenHeaderParam());
        TokenParserRequest request = new TokenParserRequest(rawToken)
                .issuerEnabled(config.issuerEnabled())
                .type(config.type())
                .verify(config.verify())
                .issuerSuffix(config.issuerSuffix());
        return tokenParserService.parseToken(request);
    }
}
