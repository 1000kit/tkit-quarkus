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

    /**
     * Returns the identifier of the default tenant.
     */
    @Override
    public String getDefaultTenantId() {
        return config.defaultTenantValue();
    }

    /**
     * Returns the current tenant identifier.
     */
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

    /**
     * Does the given tenant id represent a "root" tenant with access to all partitions?
     */
    @Override
    public boolean isRoot(String tenantId) {
        if (config.root().enabled()) {
            return config.root().value().equals(tenantId);
        }
        return TenantResolver.super.isRoot(tenantId);
    }
}
