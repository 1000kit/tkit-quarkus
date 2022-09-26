package org.tkit.quarkus.test.dbimport;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class DbImportDevServicesBuildTimeConfig {
    /**
     * If Dev Services for db-import has been explicitly enabled or disabled. Dev Services are generally enabled
     * by default, unless there is an existing configuration present.
     */
    @ConfigItem
    public Optional<Boolean> enabled = Optional.empty();

    /**
     * The db image to use.
     */
    @ConfigItem(name = "db-image-name", defaultValue = "docker.io/postgres")
    public String dbImageName;
}
