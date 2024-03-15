package org.tkit.quarkus.security;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigMapping(prefix = "tkit.security")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface SecurityConfig {

    /**
     * Disable or enabled authentication
     */
    @WithName("auth.enabled")
    @WithDefault("true")
    boolean enabled();
}
