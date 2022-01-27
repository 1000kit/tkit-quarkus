package org.tkit.quarkus.test.docker.compose.devservices;

import io.quarkus.builder.item.SimpleBuildItem;

import java.util.List;

public final class DevServicesDockerComposeBuildItem extends SimpleBuildItem {

    final List<String> services;

    public DevServicesDockerComposeBuildItem(List<String> services) {
        this.services = services;
    }

    public List<String> getServices() {
        return services;
    }
}
