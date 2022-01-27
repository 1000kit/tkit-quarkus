package org.tkit.quarkus.test.dbimport;

import io.quarkus.builder.item.SimpleBuildItem;

public final class DevServicesDbImportBuildItem extends SimpleBuildItem {

    final String url;

    public DevServicesDbImportBuildItem(String bs) {
        this.url = bs;
    }

    public String getUrl() {
        return url;
    }
}
