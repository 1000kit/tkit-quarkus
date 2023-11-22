package org.tkit.quarkus.jpa.tenant;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "tkit.jpa.tenant")
public interface ContextTenantResolverConfig {

    @WithName("default")
    @WithDefault("default")
    String defaultTenantValue();
}
