package org.tkit.quarkus.test.docker.compose.devservices;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "tkit.docker-compose", phase = ConfigPhase.BUILD_TIME)
public class DockerComposeBuildTimeConfig {

    /**
     * Configuration for DevServices. DevServices allows Quarkus to automatically start db-import in dev and test mode.
     */
    @ConfigItem
    public DevServicesDockerComposeBuildTimeConfig devservices;
}
