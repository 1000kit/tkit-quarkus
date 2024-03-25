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
        if (!config.token().enabled()) {
            return null;
        }

        var token = getToken(containerRequestContext);
        if (token == null && config.token().mandatory()) {
            throw new PrincipalTokenRequiredException();
        }
        return token;
    }

    private JsonWebToken getToken(ContainerRequestContext containerRequestContext) {

        String rawToken = containerRequestContext.getHeaders().getFirst(config.token().tokenHeaderParam());
        TokenParserRequest request = new TokenParserRequest(rawToken)
                .issuerEnabled(config.token().issuerEnabled())
                .type(config.token().type())
                .verify(config.token().verify())
                .issuerSuffix(config.token().issuerSuffix());
        return tokenParserService.parseToken(request);
    }
}
