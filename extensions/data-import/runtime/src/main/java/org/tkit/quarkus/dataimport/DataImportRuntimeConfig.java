package org.tkit.quarkus.dataimport;

import java.util.Map;

import io.quarkus.runtime.annotations.*;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigDocFilename("tkit-quarkus-data-import.adoc")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "tkit.dataimport")
public interface DataImportRuntimeConfig {

    /**
     * If set to true, the application will import data
     */
    @WithName("enabled")
    @WithDefault("true")
    boolean enabled();

    /**
     * Error message length in the database
     */
    @WithName("error-msg-length")
    @WithDefault("255")
    int errorMsgLength();

    /**
     * All configured Data Import Configurations
     */
    @WithName("configurations")
    Map<String, DataImportConfiguration> configurations();

    /**
     * A DataImportConfiguration
     */
    interface DataImportConfiguration {

        /**
         * The bean key
         */
        @WithName("bean")
        @WithDefault(" ")
        String bean();

        /**
         * The path to the data source file
         */
        @WithName("file")
        String file();

        /**
         * Set to true if the file is a classpath file.
         */
        @WithName("class-path")
        @WithDefault("false")
        boolean classpath();

        /**
         * The metadata for the execution
         */
        @WithName("metadata")
        Map<String, String> metadata();

        /**
         * If set to true, the application will run this import data
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * If set to true, the application will stop if data import failed
         */
        @WithName("stop-at-error")
        @WithDefault("false")
        boolean stopAtError();

        /**
         * If set to false, the application will not retry error import.
         */
        @WithName("retry-error-import")
        @WithDefault("true")
        boolean retryErrorImport();

    }
}