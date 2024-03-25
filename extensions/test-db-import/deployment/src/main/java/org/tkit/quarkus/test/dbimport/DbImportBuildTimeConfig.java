package org.tkit.quarkus.test.dbimport;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigDocFilename("tkit-quarkus-test-db-import.adoc")
@ConfigRoot(prefix = "tkit", name = "db-import", phase = ConfigPhase.BUILD_TIME)
public class DbImportBuildTimeConfig {

    /**
     * Configuration for DevServices. DevServices allows Quarkus to automatically start db-import in dev and test mode.
     */
    @ConfigItem
    public DbImportDevServicesBuildTimeConfig devservices;
}
