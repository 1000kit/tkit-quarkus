package org.tkit.quarkus.test.dbimport;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigDocFilename("tkit-quarkus-test-db-import.adoc")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
@ConfigMapping(prefix = "tkit.db-import")
public interface DbImportBuildTimeConfig {

    /**
     * Configuration for DevServices. DevServices allows Quarkus to automatically start db-import in dev and test mode.
     */
    @WithName("devservices")
    DbImportDevServicesBuildTimeConfig devservices();
}
