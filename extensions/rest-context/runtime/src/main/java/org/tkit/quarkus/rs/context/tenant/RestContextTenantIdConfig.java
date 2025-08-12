package org.tkit.quarkus.rs.context.tenant;

import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.*;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigDocFilename("tkit-quarkus-rest-context.adoc")
@ConfigMapping(prefix = "tkit.rs.context")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface RestContextTenantIdConfig {

    /**
     * Rest context tenant-id configuration
     */
    @WithName("tenant-id")
    TenantIdConfig tenantId();

    interface TenantIdConfig {
        /**
         * Enable or disable rest context tenant.
         */
        @WithName("enabled")
        @WithDefault("false")
        boolean enabled();

        /**
         * Make the tenant mandatory (not null)
         */
        @WithName("mandatory")
        @WithDefault("false")
        boolean mandatory();

        /**
         * Default tenant.
         */
        @WithName("default")
        Optional<String> defaultTenantId();

        /**
         * Take tenant ID from header parameter
         */
        @WithName("header-param-enabled")
        @WithDefault("false")
        boolean headerParamEnabled();

        /**
         * Tenant ID header parameter
         */
        @WithName("header-param-name")
        @WithDefault("x-tenant-id")
        String headerParamName();

        /**
         * Enable or disable custom tenant resolver service.
         */
        @WithName("custom-service-enabled")
        @WithDefault("true")
        boolean enabledCustomService();

        /**
         * Mock service config.
         */
        @WithName("mock")
        MockConfig mock();

        /**
         * Token config.
         */
        @WithName("token")
        TokenConfig token();

    }

    /**
     * Token config.
     */
    @ConfigGroup
    interface TokenConfig {
        /**
         * Enable or disable tenant token claim.
         */
        @WithName("enabled")
        @WithDefault("false")
        boolean enabled();

        /**
         * Default mock tenant
         */
        @WithName("claim-tenant-param")
        @WithDefault("tenantId")
        String claimTenantParam();

    }

    /**
     * Mock config.
     */
    @ConfigGroup
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

    }
}
