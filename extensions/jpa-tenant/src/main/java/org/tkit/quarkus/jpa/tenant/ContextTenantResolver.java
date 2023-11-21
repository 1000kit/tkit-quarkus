package org.tkit.quarkus.jpa.tenant;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;

import io.quarkus.arc.Unremovable;
import io.quarkus.hibernate.orm.PersistenceUnitExtension;
import io.quarkus.hibernate.orm.runtime.tenant.TenantResolver;

@RequestScoped
@Unremovable
@PersistenceUnitExtension
public class ContextTenantResolver implements TenantResolver {

    @Inject
    ContextTenantResolverConfig config;

    @Override
    public String getDefaultTenantId() {
        return config.defaultTenantValue();
    }

    @Override
    public String resolveTenantId() {
        Context ctx = ApplicationContext.get();
        if (ctx != null) {
            String tenantId = ctx.getTenantId();
            if (tenantId != null) {
                return tenantId;
            }
        }
        return getDefaultTenantId();
    }
}
