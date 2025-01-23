package org.tkit.quarkus.oidc.health;

import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.oidc.runtime.TenantConfigBean;
import io.quarkus.oidc.runtime.TenantConfigContext;
import io.smallrye.health.api.HealthGroup;

@Liveness
@ApplicationScoped
@HealthGroup("oidc")
public class OidcTenantHealthCheck implements HealthCheck {

    private static final Logger log = LoggerFactory.getLogger(OidcTenantHealthCheck.class);

    private static final String NAME = "OIDC tenant meta-data health check";

    @Inject
    OidcHealthCheckConfig config;

    @Inject
    TenantConfigBean tenantConfigBean;

    @Override
    public HealthCheckResponse call() {

        var builder = HealthCheckResponse.builder().name(NAME);

        if (!config.enabled()) {
            log.warn("Oidc tenant health check is disabled!");
            return builder.up().build();
        }

        if (config.defaultTenant().enabled()) {
            if (log.isDebugEnabled()) {
                debug(KIND.DEFAULT, tenantConfigBean.getDefaultTenant());
            }
            var result = down(KIND.DEFAULT, builder, tenantConfigBean.getDefaultTenant());
            if (result != null) {
                return result;
            }
        }

        if (config.dynamicTenant().enabled()) {
            var result = down(KIND.DYNAMIC, builder, tenantConfigBean.getDynamicTenantsConfig());
            if (result != null) {
                return result;
            }
        }

        if (config.staticTenant().enabled()) {
            var result = down(KIND.STATIC, builder, tenantConfigBean.getStaticTenantsConfig());
            if (result != null) {
                return result;
            }
        }

        return builder.up().build();
    }

    private HealthCheckResponse down(KIND kind, HealthCheckResponseBuilder builder,
            Map<String, TenantConfigContext> contexts) {
        if (contexts == null) {
            return null;
        }

        if (log.isDebugEnabled()) {
            for (var context : contexts.values()) {
                debug(kind, context);
            }
        }

        for (var context : contexts.values()) {
            var result = down(kind, builder, context);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private HealthCheckResponse down(KIND kind, HealthCheckResponseBuilder builder, TenantConfigContext context) {
        if (context.getOidcMetadata() != null) {
            return null;
        }

        log.error("OIDC {} tenant '{}' missing oidc meta-data. Issuer: {}", kind.getData(),
                context.getOidcTenantConfig().tenantId.orElse(null),
                context.getOidcTenantConfig().getAuthServerUrl().orElse("undefined"));

        return builder
                .down()
                .withData("tenant", context.getOidcTenantConfig().tenantId.orElse(null))
                .withData("status", "missing").build();
    }

    private void debug(KIND kind, TenantConfigContext context) {
        boolean metadata = context.getOidcMetadata() != null;
        String issuer = metadata ? context.getOidcMetadata().getIssuer() : null;
        log.debug("OIDC {} tenant '{}' OidcMetadata exits? '{}' Issuer: '{}'.", kind.getData(),
                context.getOidcTenantConfig().tenantId.orElse(null), metadata, issuer);
    }

    enum KIND {
        DEFAULT,
        STATIC,
        DYNAMIC;

        private final String data;

        KIND() {
            this.data = this.name().toLowerCase();
        }

        public String getData() {
            return data;
        }
    }
}
