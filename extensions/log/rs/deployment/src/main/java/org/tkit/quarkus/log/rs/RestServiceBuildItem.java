package org.tkit.quarkus.log.rs;

import io.quarkus.builder.item.SimpleBuildItem;

public final class RestServiceBuildItem extends SimpleBuildItem {

    RestServiceValue value;

    RestServiceBuildItem(RestServiceValue value) {
        this.value = value;
    }

}
