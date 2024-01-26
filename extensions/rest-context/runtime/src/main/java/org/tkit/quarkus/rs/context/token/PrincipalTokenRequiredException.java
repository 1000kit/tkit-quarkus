package org.tkit.quarkus.rs.context.token;

import org.tkit.quarkus.rs.context.RestContextException;

public class PrincipalTokenRequiredException extends RestContextException {

    public PrincipalTokenRequiredException() {
        super(ErrorKeys.PRINCIPAL_TOKEN_REQUIRED, "Principal token is required");
    }

    public enum ErrorKeys {

        PRINCIPAL_TOKEN_REQUIRED;
    }
}
