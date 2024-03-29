package org.tkit.quarkus.log.rs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.*;

@ConfigDocFilename("tkit-quarkus-log-rs.adoc")
@ConfigRoot(prefix = "tkit", name = "log.rs", phase = ConfigPhase.RUN_TIME)
public class RestRuntimeConfig {

    /**
     * Enabled or disable the rest log interceptor
     */
    @ConfigItem(name = "enabled", defaultValue = "true")
    public boolean enabled;

    /**
     * Enabled or disable to add principal name to the application context.
     */
    @ConfigItem(name = "principal.enabled", defaultValue = "true")
    public boolean enabledPrincipal;

    /**
     * Enabled or disable the correlation ID
     */
    @ConfigItem(name = "correlation-id-enabled", defaultValue = "true")
    public boolean correlationIdEnabled;

    /**
     * The correlation ID header
     */
    @ConfigItem(name = "correlation-id-header", defaultValue = "X-Correlation-ID")
    public String correlationIdHeader;

    /**
     * Map of MDC headers
     */
    @ConfigItem(name = "mdc-headers")
    public Map<String, String> mdcHeaders;

    /**
     * Start message
     */
    @ConfigItem(name = "start")
    public RestStartLogMessage start;

    /**
     * End message
     */
    @ConfigItem(name = "end")
    public RestEndLogMessage end;

    /**
     * Rest controller methods
     */
    @ConfigItem(name = "controller")
    public Map<String, RestControllerConfig> controller = new HashMap<>();

    /**
     * Error log configuration.
     */
    @ConfigItem(name = "error")
    public ErrorLog error;

    /**
     * Regex log configuration.
     */
    @ConfigItem(name = "regex")
    public RegexLog regex;

    /**
     * Payload log configuration.
     */
    @ConfigItem(name = "payload")
    public PayloadLog payload;

    /**
     * Payload log configuration.
     */
    @ConfigGroup
    public static class PayloadLog {

        /**
         * Enable or disable error log message
         */
        @ConfigItem(name = "enabled", defaultValue = "false")
        public boolean enabled;

        /**
         * Payload message
         * 1 - HTTP method
         * 2 - URL
         * 3 - payload
         */
        @ConfigItem(name = "template", defaultValue = "%1$s %2$s payload: %3$s")
        public String template;

        /**
         * Empty body enabled or disabled
         */
        @ConfigItem(name = "empty-body-enabled", defaultValue = "true")
        public boolean emptyBodyEnabled;

        /**
         * Empty body message
         */
        @ConfigItem(name = "empty-body-message", defaultValue = "<EMPTY BODY>")
        public String emptyBodyMessage;

        /**
         * Page message
         */
        @ConfigItem(name = "page-message", defaultValue = "...more...")
        public String pageMessage;

        /**
         * Maximum entity size
         */
        @ConfigItem(name = "max-entity-size", defaultValue = "1048576")
        public int maxEntitySize = 1048576;

        /**
         * Regex log configuration.
         */
        @ConfigItem(name = "regex")
        public RegexLog regex;
    }

    /**
     * Regex log configuration.
     */
    @ConfigGroup
    public static class RegexLog {

        /**
         * Enable or disable error log message
         */
        @ConfigItem(name = "enabled", defaultValue = "false")
        public boolean enabled;

        /**
         * Exclude request path patterns
         */
        @ConfigItem(name = "exclude")
        public Optional<List<String>> exclude;

    }

    /**
     * Error log configuration.
     */
    @ConfigGroup
    public static class ErrorLog {

        /**
         * Enable or disable error log message
         */
        @ConfigItem(name = "enabled", defaultValue = "true")
        public boolean enabled;

    }

    /**
     * Start message
     */
    @ConfigGroup
    public static class RestStartLogMessage {

        /**
         * Enable or disable start message
         */
        @ConfigItem(name = "enabled", defaultValue = "false")
        public boolean enabled;

        /**
         * Message template
         * 1 - HTTP method
         * 2 - URI
         */
        @ConfigItem(name = "template", defaultValue = "%1$s %2$s started.")
        public String template;
    }

    /**
     * End message
     */
    @ConfigGroup
    public static class RestEndLogMessage {

        /**
         * Enable or disable end message
         */
        @ConfigItem(name = "enabled", defaultValue = "true")
        public boolean enabled;

        /**
         * Message template
         * 1 - HTTP method
         * 2 - path
         * 3 - duration
         * 4 - HTTP response code
         * 5 - HTTP response name
         * 6 - URI
         */
        @ConfigItem(name = "template", defaultValue = "%1$s %2$s [%4$s] [%3$ss]")
        public String template;

        /**
         * Default MDC parameters
         */
        @ConfigItem(name = "mdc")
        public RestMdcLogConfig mdc;
    }

    @ConfigGroup
    public static class RestMdcLogConfig {

        /**
         * Enable duration time as MDC parameter
         */
        @ConfigItem(name = "enabled", defaultValue = "true")
        public boolean enabled;

        /**
         * Duration MDC key name
         */
        @ConfigItem(name = "duration.name", defaultValue = "rs-time")
        public String durationName;

        /**
         * Response status MDC key name
         */
        @ConfigItem(name = "response-status.name", defaultValue = "rs-status")
        public String responseStatusName;
    }

    /**
     * Rest-client interceptor configuration.
     */
    @ConfigItem(name = "client")
    public RestClientRuntimeConfig client;

    /**
     * Rest-client interceptor configuration.
     */
    @ConfigGroup
    public static class RestClientRuntimeConfig {

        /**
         * Enable or disable rest-client log interceptor.
         */
        @ConfigItem(name = "enabled", defaultValue = "true")
        public boolean enabled;

        /**
         * Regex log configuration.
         */
        @ConfigItem(name = "regex")
        public RegexLog regex;

        /**
         * Payload log configuration.
         */
        @ConfigItem(name = "payload")
        public PayloadLog payload;

        /**
         * Map of MDC headers
         */
        @ConfigItem(name = "mdc-headers")
        public Map<String, String> mdcHeaders;

        /**
         * Start message
         */
        @ConfigItem(name = "start")
        public RestClientStartLogMessage start;

        /**
         * End message
         */
        @ConfigItem(name = "end")
        public RestClientEndLogMessage end;

        /**
         * Error log configuration.
         */
        @ConfigItem(name = "error")
        public ErrorLog error;

    }

    /**
     * Rest-controller configuration.
     */
    @ConfigGroup
    public static class RestControllerConfig {

        /**
         * Rest controller config
         */
        @ConfigItem(name = ConfigItem.PARENT)
        public RestServiceControllerConfig config;

        /**
         * Rest controller methods
         */
        @ConfigItem(name = "method")
        public Map<String, RestServiceControllerConfig> method = new HashMap<>();

    }

    /**
     * Rest-controller configuration.
     */
    @ConfigGroup
    public static class RestServiceControllerConfig {

        /**
         * Enable or disable rest controller log
         */
        @ConfigItem(name = "log")
        public Optional<Boolean> log;

        /**
         * Enable or disable rest controller payload
         */
        @ConfigItem(name = "payload")
        public Optional<Boolean> payload;

        /**
         * Map of MDC headers
         */
        @ConfigItem(name = "mdc-headers")
        public Optional<Map<String, String>> mdcHeaders;
    }

    /**
     * Start message
     */
    @ConfigGroup
    public static class RestClientStartLogMessage {

        /**
         * Enable or disable start message
         */
        @ConfigItem(name = "enabled", defaultValue = "false")
        public boolean enabled;

        /**
         * Message template
         * 0 - HTTP method
         * 1 - URI
         */
        @ConfigItem(name = "template", defaultValue = "%1$s %2$s started.")
        public String template;
    }

    /**
     * End message
     */
    @ConfigGroup
    public static class RestClientEndLogMessage {

        /**
         * Enable or disable end message
         */
        @ConfigItem(name = "enabled", defaultValue = "true")
        public boolean enabled;

        /**
         * Message template
         * 1 - HTTP method
         * 2 - URI
         * 3 - duration
         * 4 - HTTP response code
         * 5 - HTTP response name
         */
        @ConfigItem(name = "template", defaultValue = "%1$s %2$s [%4$s] [%3$ss]")
        public String template;

        /**
         * Default MDC parameters for rest client
         */
        @ConfigItem(name = "mdc")
        public RestClientMdcLogConfig mdc;
    }

    @ConfigGroup
    public static class RestClientMdcLogConfig {

        /**
         * Enable duration time as MDC parameter
         */
        @ConfigItem(name = "enabled", defaultValue = "true")
        public boolean enabled;

        /**
         * Duration MDC key name
         */
        @ConfigItem(name = "duration.name", defaultValue = "rs-client-time")
        public String durationName;

        /**
         * Response client status MDC key name
         */
        @ConfigItem(name = "response-status.name", defaultValue = "rs-client-status")
        public String responseStatusName;
    }

}
