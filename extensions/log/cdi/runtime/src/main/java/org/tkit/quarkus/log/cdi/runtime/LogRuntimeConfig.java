package org.tkit.quarkus.log.cdi.runtime;

import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.*;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import io.smallrye.config.WithParentName;

@ConfigDocFilename("tkit-quarkus-log-cdi.adoc")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "tkit.log.cdi")
public interface LogRuntimeConfig {

    /**
     * Enable or disable interceptor
     */
    @WithName("enabled")
    @WithDefault("true")
    boolean enabled();

    /**
     * Prefix for custom data mdc entries
     */
    @WithName("custom-data.prefix")
    Optional<String> customDataPrefix();

    /**
     * Start message
     */
    @WithName("start")
    StartLogMessage start();

    /**
     * Succeed message
     */
    @WithName("succeed")
    SucceedLogMessage succeed();

    /**
     * Failed message
     */
    @WithName("failed")
    FailedLogMessage failed();

    /**
     * Return void method template
     */
    @WithName("return-void-template")
    @WithDefault("void")
    String returnVoidTemplate();

    /**
     * Service configuration
     */
    @WithName("service")
    Map<String, ServiceConfig> service();

    /**
     * Mdc error key for FBN error code
     */
    @WithName("mdc.errorKey")
    @WithDefault("errorNumber")
    String errorNumberKey();

    /**
     * Start message
     */
    interface StartLogMessage {

        /**
         * Enable or disable start message
         */
        @WithName("enabled")
        @WithDefault("false")
        boolean enabled();

        /**
         * Message template
         * 0 - method
         * 1 - parameters
         */
        @WithName("template")
        @WithDefault("%1$s(%2$s) started.")
        String template();
    }

    /**
     * Succeed message
     */
    interface SucceedLogMessage {

        /**
         * Enable or disable end message
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * Message template
         * 1 - method
         * 2 - parameters
         * 3 - return value
         * 4 - time
         */
        @WithName("template")
        @WithDefault("%1$s(%2$s):%3$s [%4$.3fs]")
        String template();
    }

    /**
     * Failed message
     */
    interface FailedLogMessage {

        /**
         * Enable or disable end message
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * Message template
         * 1 - method
         * 2 - parameters
         * 3 - return value
         * 4 - time
         */
        @WithName("template")
        @WithDefault("%1$s(%2$s) throw %3$s [%4$.3fs]")
        String template();
    }

    /**
     * Service configuration.
     */
    interface ServiceConfig {

        /**
         * Service controller config
         */
        @WithParentName
        LogServiceConfig config();

        /**
         * Service methods
         */
        @WithName("method")
        Map<String, MethodConfig> method();

    }

    /**
     * Method configuration.
     */
    interface MethodConfig {

        /**
         * Service controller config
         */
        @WithParentName
        LogServiceConfig config();

        /**
         * Return mask
         */
        @WithName("return-mask")
        Optional<String> returnMask();

        /**
         * Exclude parameters
         */
        @WithName("param")
        Map<Short, String> params();

    }

    /**
     * Rest-controller configuration.
     */
    interface LogServiceConfig {

        /**
         * Enable or disable service log
         */
        @WithName("log")
        Optional<Boolean> log();

        /**
         * Enable or disable service stacktrace
         */
        @WithName("stacktrace")
        Optional<Boolean> stacktrace();

    }
}