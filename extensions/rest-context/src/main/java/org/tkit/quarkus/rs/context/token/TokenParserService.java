package org.tkit.quarkus.rs.context.token;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.lang.JoseException;

import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;

@RequestScoped
public class TokenParserService {

    @Inject
    JWTAuthContextInfo authContextInfo;

    @Inject
    JWTParser parser;

    public JsonWebToken parseToken(TokenParserRequest request) throws TokenException {
        if (request == null) {
            return null;
        }
        try {
            return parseTokenRequest(request);
        } catch (Exception ex) {
            throw new TokenException(ErrorKeys.ERROR_PARSE_TOKEN, "Error parse raw token", ex);
        }
    }

    private JsonWebToken parseTokenRequest(TokenParserRequest request)
            throws InvalidJwtException, JoseException, MalformedClaimException, ParseException {

        if (request.getRawToken() == null) {
            return null;
        }

        if (request.isVerify()) {
            var info = authContextInfo;

            if (request.isIssuerEnabled()) {
                var jws = (JsonWebSignature) JsonWebStructure.fromCompactSerialization(request.getRawToken());
                var jwtClaims = JwtClaims.parse(jws.getUnverifiedPayload());
                var publicKeyLocation = jwtClaims.getIssuer() + request.getIssuerSuffix();
                info = new JWTAuthContextInfo(authContextInfo);
                info.setPublicKeyLocation(publicKeyLocation);
            }
            return parser.parse(request.getRawToken(), info);
        }

        var jws = (JsonWebSignature) JsonWebStructure.fromCompactSerialization(request.getRawToken());
        var jwtClaims = JwtClaims.parse(jws.getUnverifiedPayload());
        return new DefaultJWTCallerPrincipal(request.getRawToken(), request.getType(), jwtClaims);
    }

    public enum ErrorKeys {

        ERROR_PARSE_TOKEN;
    }
}
