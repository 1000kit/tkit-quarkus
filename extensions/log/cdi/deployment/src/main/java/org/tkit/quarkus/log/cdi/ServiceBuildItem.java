package org.tkit.quarkus.log.cdi;

import io.quarkus.builder.item.SimpleBuildItem;

public final class ServiceBuildItem extends SimpleBuildItem {

    ServiceValue values;

    public ServiceBuildItem(ServiceValue values) {
        this.values = values;
    }
}
