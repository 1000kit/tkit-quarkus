package org.tkit.quarkus.log.cdi.runtime;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

import java.util.Optional;

@ConfigRoot(name = "tkit.log.cdi", phase = ConfigPhase.RUN_TIME)
public class LogRuntimeConfig {

    /**
     * Prefix for custom data mdc entries
     */
    @ConfigItem(name = "custom-data.prefix")
    public Optional<String> customDataPrefix;

    /**
     * Enable or disable protected method
     */
    @ConfigItem(name = "enable-protected-method", defaultValue = "false")
    public boolean enableProtectedMethod;

}
