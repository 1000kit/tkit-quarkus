package org.tkit.quarkus.dataimport;

import java.util.Map;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(prefix = "tkit", name = "dataimport", phase = ConfigPhase.RUN_TIME)
public class DataImportRuntimeConfig {

    /**
     * If set to true, the application will import data
     */
    @ConfigItem(name = "enabled", defaultValue = "true")
    boolean enabled = true;

    /**
     * Error message length in the database
     */
    @ConfigItem(name = "error-msg-length", defaultValue = "255")
    int errorMsgLength = 255;

    /**
     * All configured Data Import Configurations
     */
    @ConfigItem(name = "configurations")
    Map<String, DataImportConfiguration> configurations;

    /**
     * A DataImportConfiguration
     */
    @ConfigGroup
    public static class DataImportConfiguration {

        /**
         * The bean key
         */
        @ConfigItem(name = "bean", defaultValue = " ")
        String bean;

        /**
         * The path to the data source file
         */
        @ConfigItem(name = "file")
        String file;

        /**
         * The metadata for the execution
         */
        @ConfigItem(name = "metadata")
        Map<String, String> metadata;

        /**
         * If set to true, the application will run this import data
         */
        @ConfigItem(name = "enabled", defaultValue = "true")
        boolean enabled = true;

        /**
         * If set to true, the application will stop if data import failed
         */
        @ConfigItem(name = "stop-at-error", defaultValue = "false")
        boolean stopAtError = false;

        /**
         * If set to false, the application will not retry error import.
         */
        @ConfigItem(name = "retry-error-import", defaultValue = "true")
        boolean retryErrorImport = true;

        @Override
        public String toString() {
            return "Configuration{" +
                    "bean='" + bean + '\'' +
                    ",file='" + file + '\'' +
                    ",metadata=" + metadata +
                    ",enabled=" + enabled +
                    ",stopAtError=" + stopAtError +
                    '}';
        }
    }
}
