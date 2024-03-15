package org.tkit.quarkus.jpa.tenant;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "tkit.jpa.tenant")
public interface ContextTenantResolverConfig {

    /**
     * Default tenant resolver value.
     */
    @WithName("default")
    @WithDefault("default")
    String defaultTenantValue();
}
