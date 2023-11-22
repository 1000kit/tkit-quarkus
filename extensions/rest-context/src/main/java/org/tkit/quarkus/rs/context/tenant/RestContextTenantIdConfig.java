package org.tkit.quarkus.rs.context.tenant;

import java.util.Map;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigMapping(prefix = "tkit.rs.context.tenant-id")
public interface RestContextTenantIdConfig {

    @WithName("enabled")
    @WithDefault("false")
    boolean enabled();

    @WithName("default")
    @WithDefault("default")
    String defaultTenantId();

    @WithName("header-param-enabled")
    @WithDefault("false")
    boolean headerParamEnabled();

    @WithName("header-param-name")
    @WithDefault("tenant-id")
    String headerParamName();

    @WithName("mock")
    MockConfig mock();

    interface MockConfig {

        /**
         * Enable or disable tenant mock service.
         */
        @WithName("enabled")
        @WithDefault("false")
        boolean enabled();

        /**
         * Default mock tenant
         */
        @WithName("default-tenant")
        @WithDefault("default")
        String defaultTenant();

        /**
         * Mock data
         */
        @WithName("data")
        Map<String, String> data();

        /**
         * Token organization claim id
         */
        @WithName("claim-org-id")
        @WithDefault("orgId")
        String claimOrgId();

        /**
         * Token header parameter for mock service
         */
        @WithName("token-header-param")
        @WithDefault("apm-principal-token")
        String tokenHeaderParam();
    }
}
