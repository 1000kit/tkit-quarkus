package org.tkit.quarkus.rs.context.tenant;

import org.tkit.quarkus.rs.context.RestContextException;

public class TenantRequiredException extends RestContextException {

    public TenantRequiredException() {
        super(ErrorKeys.TENANT_REQUIRED, "Tenant is required");
    }

    public enum ErrorKeys {

        TENANT_REQUIRED;
    }
}
