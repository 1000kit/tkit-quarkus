package org.tkit.quarkus.rs.context;

public class PrincipalRequiredException extends RestContextException {

    public PrincipalRequiredException() {
        super(ErrorKeys.PRINCIPAL_REQUIRED, "Principal is required");
    }

    public enum ErrorKeys {

        PRINCIPAL_REQUIRED;
    }
}
