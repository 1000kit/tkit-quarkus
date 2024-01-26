package org.tkit.quarkus.log.cdi.deployment;

import org.tkit.quarkus.log.cdi.ServiceValue;

import io.quarkus.builder.item.SimpleBuildItem;

public final class ServiceBuildItem extends SimpleBuildItem {

    ServiceValue values;

    public ServiceBuildItem(ServiceValue values) {
        this.values = values;
    }
}
