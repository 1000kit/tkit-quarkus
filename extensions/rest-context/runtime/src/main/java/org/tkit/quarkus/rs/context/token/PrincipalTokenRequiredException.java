package org.tkit.quarkus.rs.context.token;

import org.tkit.quarkus.rs.context.RestContextException;

public class PrincipalTokenRequiredException extends RestContextException {

    public PrincipalTokenRequiredException(ErrorKeys errorKeys, String message) {
        super(errorKeys, message);
    }

    public enum ErrorKeys {

        PRINCIPAL_TOKEN_WRONG_ISSUER,

        PRINCIPAL_TOKEN_REQUIRED;
    }
}
