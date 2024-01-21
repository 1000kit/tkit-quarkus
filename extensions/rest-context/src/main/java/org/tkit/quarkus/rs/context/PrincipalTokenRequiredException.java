package org.tkit.quarkus.rs.context;

public class PrincipalTokenRequiredException extends RestContextException {

    public PrincipalTokenRequiredException() {
        super(ErrorKeys.PRINCIPAL_TOKEN_REQUIRED, "Principal token is required");
    }

    public enum ErrorKeys {

        PRINCIPAL_TOKEN_REQUIRED;
    }
}
