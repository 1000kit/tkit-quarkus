package org.tkit.quarkus.test.docker.compose.devservices;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

import java.util.Optional;

@ConfigGroup
public class DevServicesDockerComposeBuildTimeConfig {
    /**
     * If Dev Services for db-import has been explicitly enabled or disabled. Dev Services are generally enabled
     * by default, unless there is an existing configuration present.
     */
    @ConfigItem
    public Optional<Boolean> enabled = Optional.empty();

    /**
     * Indicates if the db-import managed by Quarkus Dev Services is shared.
     * When shared, Quarkus looks for running containers using label-based service discovery.
     * If a matching container is found, it is used, and so a second one is not started.
     * Otherwise, Dev Services for db-import starts a new container.
     * <p>
     * The discovery uses the {@code quarkus-dev-service-db-import} label.
     * The value is configured using the {@code service-name} property.
     * <p>
     * Container sharing is only used in dev mode.
     */
    @ConfigItem(defaultValue = "true")
    public boolean shared;




    /**
     * Docker compose file.
     */
    @ConfigItem(name = "docker-compose-file", defaultValue = "src/test/resources/docker-compose.yml")
    public String dockerComposeFile;
}
