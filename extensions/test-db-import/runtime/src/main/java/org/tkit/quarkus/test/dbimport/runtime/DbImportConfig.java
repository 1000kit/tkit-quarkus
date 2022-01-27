package org.tkit.quarkus.test.dbimport.runtime;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "tkit.db-import", phase = ConfigPhase.RUN_TIME)
public class DbImportConfig {

    /**
     * Database import service URL
     */
    @ConfigItem(name = "url", defaultValue = "http://docker:8081")
    public String url;

}
