# tkit-quarkus-log-rs

Quarkus extension for HTTP request logging (client & server).

Maven dependency
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-log-rs</artifactId>
</dependency>
```
### Configuration

```properties
# Enabled or disable the rest log interceptor
tkit.log.rs.enabled=true
# Enabled or disable the correlation ID
tkit.log.rs.correlation-id-enabled=true
# The correlation ID header name
tkit.log.rs.correlation-id-header=X-Correlation-ID
# Map of MDC headers
tkit.log.rs.mdc-headers.<http-header-name>=<mdc-name>
# Enable or disable start message
tkit.log.rs.start.enabled=false
# Start log message template (%1 - HTTP method, %2 - URI)
tkit.log.rs.start.template=%1$s %2$s started.
# Enable or disable end message
tkit.log.rs.end.enabled=true
# End log message template (%1 - HTTP method, %2 - Path, %3 - Duration, %4 - HTTP response code, %5 - HTTP response name, %6 - URI)
tkit.log.rs.end.template=%1$s %2$s [%4$s] [%3$ss]
# Enable duration time as MDC parameter
tkit.log.rs.end.mdc.duration.enabled=true
# MDC duration name
tkit.log.rs.end.mdc.duration.name=tkit_time
# Enable or disable error log message
tkit.log.rs.error.enabled=true
# Enable or disable error log message
tkit.log.rs.regex.enabled=false
# Exclude request path patterns
tkit.log.rs.regex.exclude=
# Enable or disable error log message
tkit.log.rs.payload.enabled=true
# Payload log template (%1 - HTTP method, %2 - URL, %3 - Body)
tkit.log.rs.payload.template=%1$s %2$s payload: %3$s
# Empty body enabled or disabled
tkit.log.rs.payload.empty-body-enabled=true
# Empty body message
tkit.log.rs.payload.empty-body-message=<EMPTY BODY>
# Body content page last line message
tkit.log.rs.payload.page-message=...more...
# Maximum entity size
tkit.log.rs.payload.max-entity-size=1048576
# Enable or disable error log message
tkit.log.rs.payload.regex.enabled=false
# Exclude request path patterns
tkit.log.rs.payload.regex.exclude=
# Enabled or disable controller log (Optional)
tkit.log.rs.controller.<fully-qualified-class-name|config-key>.log=
# Enable or disable rest controller payload (Optional)
tkit.log.rs.controller.<fully-qualified-class-name|config-key>.payload=
# Map of MDC headers for the controller (Optional)
tkit.log.rs.controller.<fully-qualified-class-name|config-key>.mdc-headers.<http-header-name>=<mdc-name>
# Enabled or disable controller method log (Optional)
tkit.log.rs.controller.<fully-qualified-class-name|config-key>.<method-name>.log=
# Enable or disable rest controller method payload (Optional)
tkit.log.rs.controller.<fully-qualified-class-name|config-key>.<method-name>.payload=
# Map of MDC headers for the controller method (Optional)
tkit.log.rs.controller.<fully-qualified-class-name|config-key>.<method-name>.mdc-headers.<http-header-name>=<mdc-name>
# Enable or disable rest-client log interceptor.
tkit.log.rs.client.enabled=true
# Enable or disable error log message
tkit.log.rs.client.regex.enabled=false
# Exclude request path patterns
tkit.log.rs.client.regex.exclude=
# Enable or disable error log message
tkit.log.rs.client.payload.enabled=true
# Payload log template (%1 - HTTP method, %2 - URL, %3 - Body)
tkit.log.rs.client.payload.template=%1$s %2$s payload: %3$s
# Empty body enabled or disabled
tkit.log.rs.client.payload.empty-body-enabled=true
# Empty body message
tkit.log.rs.client.payload.empty-body-message=<EMPTY BODY>
# Body content page last line message
tkit.log.rs.client.payload.page-message=...more...
# Maximum entity size
tkit.log.rs.client.payload.max-entity-size=1048576
# Enable or disable error log message
tkit.log.rs.client.payload.regex.enabled=false
# Exclude request path patterns
tkit.log.rs.client.payload.regex.exclude=
# Map of MDC headers
tkit.log.rs.client.mdc-headers.<http-header-name>=<mdc-name>
# Enable or disable start message
tkit.log.rs.client.start.enabled=false
# Client start log message template (%1 - HTTP method, %1 - URI)
tkit.log.rs.client.start.template=%1$s %2$s started.
# Enable or disable client end message
tkit.log.rs.client.end.enabled=true
# Client end message template (%1 - HTTP method, %2 - URI, %3 - Duration, %4 - HTTP response code, %5 - HTTP response name)
tkit.log.rs.client.end.template=%1$s %2$s [%4$s] [%3$ss]
# Enable or disable error log message
tkit.log.rs.client.error.enabled=true
```

