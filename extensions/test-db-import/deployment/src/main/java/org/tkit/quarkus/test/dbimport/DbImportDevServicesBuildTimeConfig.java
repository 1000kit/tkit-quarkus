package org.tkit.quarkus.test.dbimport;

import java.util.Optional;

import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

public interface DbImportDevServicesBuildTimeConfig {
    /**
     * If Dev Services for db-import has been explicitly enabled or disabled. Dev Services are generally enabled
     * by default, unless there is an existing configuration present.
     */
    @WithName("enabled")
    Optional<Boolean> enabled();

    /**
     * The db image to use.
     */
    @WithName("db-image-name")
    @WithDefault("docker.io/postgres")
    String dbImageName();
}
