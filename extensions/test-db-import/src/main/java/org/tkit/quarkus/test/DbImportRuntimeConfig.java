package org.tkit.quarkus.test;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigDocFilename("tkit-quarkus-test-db-import.adoc")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "tkit.test.db-import")
public interface DbImportRuntimeConfig {

    /**
     * If db-import has been explicitly enabled or disabled.
     */
    @WithName("enabled")
    @WithDefault("true")
    boolean enabled();
}
