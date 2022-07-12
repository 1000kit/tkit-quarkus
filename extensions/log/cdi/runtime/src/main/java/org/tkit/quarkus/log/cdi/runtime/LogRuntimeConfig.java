package org.tkit.quarkus.log.cdi.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "tkit.log.cdi", phase = ConfigPhase.RUN_TIME)
public class LogRuntimeConfig {

    /**
     * Enable or disable interceptor
     */
    @ConfigItem(name = "enabled", defaultValue = "true")
    public boolean enabled;

    /**
     * Prefix for custom data mdc entries
     */
    @ConfigItem(name = "custom-data.prefix")
    public Optional<String> customDataPrefix;

    /**
     * Start message
     */
    @ConfigItem(name = "start")
    public StartLogMessage start;

    /**
     * Succeed message
     */
    @ConfigItem(name = "succeed")
    public SucceedLogMessage succeed;

    /**
     * Failed message
     */
    @ConfigItem(name = "failed")
    public FailedLogMessage failed;

    /**
     * Return void method template
     */
    @ConfigItem(name = "return-void-template", defaultValue = "void")
    public String returnVoidTemplate;

    /**
     * Service configuration
     */
    @ConfigItem(name = "service")
    public Map<String, ServiceConfig> service = new HashMap<>();

    /**
     * Start message
     */
    @ConfigGroup
    public static class StartLogMessage {

        /**
         * Enable or disable start message
         */
        @ConfigItem(name = "enabled", defaultValue = "false")
        public boolean enabled;

        /**
         * Message template
         * 0 - method
         * 1 - parameters
         */
        @ConfigItem(name = "template", defaultValue = "%1$s %2$s started.")
        public String template;
    }

    /**
     * Succeed message
     */
    @ConfigGroup
    public static class SucceedLogMessage {

        /**
         * Enable or disable end message
         */
        @ConfigItem(name = "enabled", defaultValue = "true")
        public boolean enabled;

        /**
         * Message template
         * 1 - method
         * 2 - parameters
         * 3 - return value
         * 4 - time
         */
        @ConfigItem(name = "template", defaultValue = "%1$s(%2$s):%3$s [%4$.3fs]")
        public String template;
    }

    /**
     * Failed message
     */
    @ConfigGroup
    public static class FailedLogMessage {

        /**
         * Enable or disable end message
         */
        @ConfigItem(name = "enabled", defaultValue = "true")
        public boolean enabled;

        /**
         * Message template
         * 1 - method
         * 2 - parameters
         * 3 - return value
         * 4 - time
         */
        @ConfigItem(name = "template", defaultValue = "%1$s(%2$s) throw %3$s [%4$.3fs]")
        public String template;
    }

    /**
     * Service configuration.
     */
    @ConfigGroup
    public static class ServiceConfig {

        /**
         * Service controller config
         */
        @ConfigItem(name = ConfigItem.PARENT)
        public LogServiceConfig config;

        /**
         * Service methods
         */
        @ConfigItem(name = "method")
        public Map<String, MethodConfig> method = new HashMap<>();

    }

    /**
     * Method configuration.
     */
    @ConfigGroup
    public static class MethodConfig {

        /**
         * Service controller config
         */
        @ConfigItem(name = ConfigItem.PARENT)
        public LogServiceConfig config;

        /**
         * Return mask
         */
        @ConfigItem(name = "return-mask")
        public Optional<String> returnMask;

        /**
         * Exclude parameters
         */
        @ConfigItem(name = "param")
        public Optional<Map<Short, String>> params;

    }

    /**
     * Rest-controller configuration.
     */
    @ConfigGroup
    public static class LogServiceConfig {

        /**
         * Enable or disable service log
         */
        @ConfigItem(name = "log")
        public Optional<Boolean> log;

        /**
         * Enable or disable service stacktrace
         */
        @ConfigItem(name = "stacktrace")
        public Optional<Boolean> stacktrace;

    }
}
