package org.tkit.quarkus.log.rs;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.*;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import io.smallrye.config.WithParentName;

@ConfigDocFilename("tkit-quarkus-log-rs.adoc")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "tkit.log.rs")
public interface RestRuntimeConfig {

    /**
     * Enabled or disable the rest log interceptor
     */
    @WithName("enabled")
    @WithDefault("true")
    boolean enabled();

    /**
     * Enabled or disable to add principal name to the application context.
     */
    @WithName("principal.enabled")
    @WithDefault("true")
    boolean enabledPrincipal();

    /**
     * Enabled or disable the correlation ID
     */
    @WithName("correlation-id-enabled")
    @WithDefault("true")
    boolean correlationIdEnabled();

    /**
     * The correlation ID header
     */
    @WithName("correlation-id-header")
    @WithDefault("X-Correlation-ID")
    String correlationIdHeader();

    /**
     * Map of MDC headers
     */
    @WithName("mdc-headers")
    Map<String, String> mdcHeaders();

    /**
     * Start message
     */
    @WithName("start")
    RestStartLogMessage start();

    /**
     * End message
     */
    @WithName("end")
    RestEndLogMessage end();

    /**
     * Rest controller methods
     */
    @WithName("controller")
    Map<String, RestControllerConfig> controller();

    /**
     * Error log configuration.
     */
    @WithName("error")
    ErrorLog error();

    /**
     * Regex log configuration.
     */
    @WithName("regex")
    RegexLog regex();

    /**
     * Payload log configuration.
     */
    @WithName("payload")
    PayloadLog payload();

    /**
     * Payload log configuration.
     */
    interface PayloadLog {

        /**
         * Enable or disable error log message
         */
        @WithName("enabled")
        @WithDefault("false")
        boolean enabled();

        /**
         * Payload message
         * 1 - HTTP method
         * 2 - URL
         * 3 - payload
         */
        @WithName("template")
        @WithDefault("%1$s %2$s payload: %3$s")
        String template();

        /**
         * Empty body enabled or disabled
         */
        @WithName("empty-body-enabled")
        @WithDefault("true")
        boolean emptyBodyEnabled();

        /**
         * Empty body message
         */
        @WithName("empty-body-message")
        @WithDefault("<EMPTY BODY>")
        String emptyBodyMessage();

        /**
         * Page message
         */
        @WithName("page-message")
        @WithDefault("...more...")
        String pageMessage();

        /**
         * Maximum entity size
         */
        @WithName("max-entity-size")
        @WithDefault("1048576")
        int maxEntitySize();

        /**
         * Regex log configuration.
         */
        @WithName("regex")
        RegexLog regex();
    }

    /**
     * Regex log configuration.
     */
    interface RegexLog {

        /**
         * Enable or disable error log message
         */
        @WithName("enabled")
        @WithDefault("false")
        boolean enabled();

        /**
         * Exclude request path patterns
         */
        @WithName("exclude")
        Optional<List<String>> exclude();

    }

    /**
     * Error log configuration.
     */
    interface ErrorLog {

        /**
         * Enable or disable error log message
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

    }

    /**
     * Start message
     */
    interface RestStartLogMessage {

        /**
         * Enable or disable start message
         */
        @WithName("enabled")
        @WithDefault("false")
        boolean enabled();

        /**
         * Message template
         * 1 - HTTP method
         * 2 - URI
         */
        @WithName("template")
        @WithDefault("%1$s %2$s started.")
        String template();
    }

    /**
     * End message
     */
    interface RestEndLogMessage {

        /**
         * Enable or disable end message
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * Message template
         * 1 - HTTP method
         * 2 - path
         * 3 - duration
         * 4 - HTTP response code
         * 5 - HTTP response name
         * 6 - URI
         */
        @WithName("template")
        @WithDefault("%1$s %2$s [%4$s] [%3$ss]")
        String template();

        /**
         * Default MDC parameters
         */
        @WithName("mdc")
        RestMdcLogConfig mdc();
    }

    interface RestMdcLogConfig {

        /**
         * Enable duration time as MDC parameter
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * Duration MDC key name
         */
        @WithName("duration.name")
        @WithDefault("rs-time")
        String durationName();

        /**
         * Response status MDC key name
         */
        @WithName("response-status.name")
        @WithDefault("rs-status")
        String responseStatusName();
    }

    /**
     * Rest-client interceptor configuration.
     */
    @WithName("client")
    RestClientRuntimeConfig client();

    /**
     * Rest-client interceptor configuration.
     */
    interface RestClientRuntimeConfig {

        /**
         * Enable or disable rest-client log interceptor.
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * Regex log configuration.
         */
        @WithName("regex")
        RegexLog regex();

        /**
         * Payload log configuration.
         */
        @WithName("payload")
        PayloadLog payload();

        /**
         * Map of MDC headers
         */
        @WithName("mdc-headers")
        Map<String, String> mdcHeaders();

        /**
         * Start message
         */
        @WithName("start")
        RestClientStartLogMessage start();

        /**
         * End message
         */
        @WithName("end")
        RestClientEndLogMessage end();

        /**
         * Error log configuration.
         */
        @WithName("error")
        ErrorLog error();

    }

    /**
     * Rest-controller configuration.
     */
    interface RestControllerConfig {

        /**
         * Rest controller config
         */
        @WithParentName
        RestServiceControllerConfig config();

        /**
         * Rest controller methods
         */
        @WithName("method")
        Map<String, RestServiceControllerConfig> method();

    }

    /**
     * Rest-controller configuration.
     */
    interface RestServiceControllerConfig {

        /**
         * Enable or disable rest controller log
         */
        @WithName("log")
        Optional<Boolean> log();

        /**
         * Enable or disable rest controller payload
         */
        @WithName("payload")
        Optional<Boolean> payload();

    }

    /**
     * Start message
     */
    @ConfigGroup
    interface RestClientStartLogMessage {

        /**
         * Enable or disable start message
         */
        @WithName("enabled")
        @WithDefault("false")
        boolean enabled();

        /**
         * Message template
         * 0 - HTTP method
         * 1 - URI
         */
        @WithName("template")
        @WithDefault("%1$s %2$s started.")
        String template();
    }

    /**
     * End message
     */
    interface RestClientEndLogMessage {

        /**
         * Enable or disable end message
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * Message template
         * 1 - HTTP method
         * 2 - URI
         * 3 - duration
         * 4 - HTTP response code
         * 5 - HTTP response name
         */
        @WithName("template")
        @WithDefault("%1$s %2$s [%4$s] [%3$ss]")
        String template();

        /**
         * Default MDC parameters for rest client
         */
        @WithName("mdc")
        RestClientMdcLogConfig mdc();
    }

    interface RestClientMdcLogConfig {

        /**
         * Enable duration time as MDC parameter
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * Duration MDC key name
         */
        @WithName("duration.name")
        @WithDefault("rs-client-time")
        String durationName();

        /**
         * Response client status MDC key name
         */
        @WithName("response-status.name")
        @WithDefault("rs-client-status")
        String responseStatusName();
    }

}