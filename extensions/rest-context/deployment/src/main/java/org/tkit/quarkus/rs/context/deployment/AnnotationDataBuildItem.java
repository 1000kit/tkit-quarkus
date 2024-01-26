package org.tkit.quarkus.rs.context.deployment;

import org.tkit.quarkus.rs.context.runtime.TenantAnnotationData;

import io.quarkus.builder.item.SimpleBuildItem;

public final class AnnotationDataBuildItem extends SimpleBuildItem {

    TenantAnnotationData data;

    public AnnotationDataBuildItem(TenantAnnotationData data) {
        this.data = data;
    }
}
