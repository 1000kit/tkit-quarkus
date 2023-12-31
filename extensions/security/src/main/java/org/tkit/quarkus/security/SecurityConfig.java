package org.tkit.quarkus.security;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "tkit.security")
public interface SecurityConfig {

    @WithName("auth.enabled")
    @WithDefault("true")
    boolean enabled();
}
