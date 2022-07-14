package org.tkit.quarkus.dataimport.deployment;

import io.quarkus.builder.item.MultiBuildItem;

public final class DataImportBeanInfo extends MultiBuildItem {

    private final String bean;

    private final String key;

    public DataImportBeanInfo(String bean, String key) {
        this.bean = bean;
        this.key = key;
    }

    public String getBean() {
        return bean;
    }

    public String getKey() {
        return key;
    }

}
