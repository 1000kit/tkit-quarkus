package org.tkit.quarkus.rs.context.token;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.security.UnauthorizedException;

@ApplicationScoped
public class TokenContextService {

    private static final Logger log = LoggerFactory.getLogger(TokenContextService.class);

    @Inject
    TokenParserService tokenParserService;

    @Inject
    TokenContextConfig config;

    @Inject
    JsonWebToken accessToken;

    public JsonWebToken getRestContextPrincipalToken(ContainerRequestContext containerRequestContext) {
        if (!config.token().enabled()) {
            return null;
        }

        // parse principal token
        var token = getToken(containerRequestContext);

        if (token == null) {
            // check if principal token is mandatory
            if (config.token().mandatory()) {
                log.error("Principal token is required for the request.");
                if (config.token().requiredErrorUnauthorized()) {
                    throw new UnauthorizedException("Principal token is required");
                }
                throw new PrincipalTokenRequiredException(PrincipalTokenRequiredException.ErrorKeys.PRINCIPAL_TOKEN_REQUIRED,
                        "Principal token is required");
            }
        } else {
            // compare access token and principal token issuer
            if (config.token().checkTokensIssuer() && accessToken.getRawToken() != null) {
                if (!token.getIssuer().equals(accessToken.getIssuer())) {
                    log.error("Principal token has undefined issuer compare to access token.");
                    if (config.token().checkTokensIssuerErrorUnauthorized()) {
                        throw new UnauthorizedException("Undefined principal token issuer");
                    }
                    throw new PrincipalTokenRequiredException(
                            PrincipalTokenRequiredException.ErrorKeys.PRINCIPAL_TOKEN_WRONG_ISSUER,
                            "Undefined principal token issuer");
                }
            }
        }

        return token;
    }

    private JsonWebToken getToken(ContainerRequestContext containerRequestContext) {

        var tc = config.token();
        String rawToken = containerRequestContext.getHeaders().getFirst(tc.tokenHeaderParam());
        if (rawToken == null) {
            return null;
        }

        TokenParserRequest request = new TokenParserRequest(rawToken)
                .issuerEnabled(tc.issuerEnabled())
                .issuerSuffix(tc.issuerSuffix())
                .issuerUrl(tc.issuerUrl().orElse(null))
                .type(tc.type())
                .verify(tc.verify());

        var issuers = tc.issuers();
        if (issuers != null && !issuers.isEmpty()) {
            issuers.forEach((k, v) -> {
                if (v.enabled()) {
                    request.addIssuerParserRequest(k, new TokenParserRequest.IssuerParserRequest()
                            .url(v.url())
                            .publicKeyLocationUrl(v.publicKeyLocationUrl().orElse(null))
                            .publicKeyLocationEnabled(v.publicKeyLocationEnabled())
                            .publicKeyLocationSuffix(v.publicKeyLocationSuffix()));
                }
            });
        }

        try {
            return tokenParserService.parseToken(request);
        } catch (TokenException ex) {
            if (tc.parserErrorUnauthorized()) {
                throw new UnauthorizedException(ex);
            }
            throw ex;
        }
    }
}
