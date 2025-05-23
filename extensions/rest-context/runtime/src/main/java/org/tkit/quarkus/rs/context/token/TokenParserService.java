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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;

@RequestScoped
public class TokenParserService {

    private static final Logger log = LoggerFactory.getLogger(TokenParserService.class);

    @Inject
    JWTAuthContextInfo authContextInfo;

    @Inject
    JWTParser parser;

    /**
     * Parse RAW web token.
     *
     * @param request request data for the parser.
     * @return valid token
     * @throws TokenException if parsing or validation failed.
     */
    public JsonWebToken parseToken(TokenParserRequest request) throws TokenException {
        if (request == null) {
            return null;
        }
        try {
            return parseTokenRequest(request);
        } catch (TokenException tex) {
            throw tex;
        } catch (Exception ex) {
            throw new TokenException(ErrorKeys.ERROR_PARSE_TOKEN, "Error parse raw token", ex);
        }
    }

    private JsonWebToken parseTokenRequest(TokenParserRequest request)
            throws InvalidJwtException, JoseException, MalformedClaimException, ParseException {

        if (request.getRawToken() == null) {
            return null;
        }

        var jws = (JsonWebSignature) JsonWebStructure.fromCompactSerialization(request.getRawToken());
        var jwtClaims = JwtClaims.parse(jws.getUnverifiedPayload());

        if (request.isVerify()) {
            var issuer = jwtClaims.getIssuer();
            var publicLocationSuffix = request.getIssuerSuffix();
            var publicLocationEnabled = request.isIssuerEnabled();

            if (!request.getIssuerParserRequests().isEmpty()) {

                var ir = request.getIssuerParserRequests().entrySet().stream()
                        .filter(e -> issuer.equals(e.getValue().getUrl()))
                        .findAny();

                if (ir.isPresent()) {
                    var pls = ir.get().getValue();
                    publicLocationSuffix = pls.getPublicKeyLocationSuffix();
                    publicLocationEnabled = pls.getPublicKeyLocationEnabled();
                } else {
                    log.error("Undefined issuer found in token. Issuer: '{}'", issuer);
                    throw new TokenException(ErrorKeys.UNDEFINED_ISSUER_FOUND_IN_TOKEN, "Undefined issuer found in token");
                }

            }

            var info = authContextInfo;
            if (publicLocationEnabled) {
                info = new JWTAuthContextInfo(authContextInfo);
                info.setPublicKeyLocation(issuer + publicLocationSuffix);
            }
            return parser.parse(request.getRawToken(), info);
        }
        return new DefaultJWTCallerPrincipal(request.getRawToken(), request.getType(), jwtClaims);
    }

    public enum ErrorKeys {

        UNDEFINED_ISSUER_FOUND_IN_TOKEN,

        ERROR_PARSE_TOKEN;
    }
}
