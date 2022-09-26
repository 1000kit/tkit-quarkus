# tkit-quarkus-log-json

Custom JSON log formatter that provides additional features not included in [Official Quarkus json logger](https://quarkus.io/guides/logging#json-logging). 
Make sure you only include this if you need those extra features, otherwise use the official extension.

Maven dependency
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-log-json</artifactId>
</dependency>
```
### Configuration

```properties
# Determine whether to enable the JSON console formatting extension, which disables "normal" console formatting.
tkit.log.json.enabled=true
# Enable "pretty printing" of the JSON record. Note that some JSON parsers will fail to read pretty printed output.
tkit.log.json.pretty-print=false
# The date format to use. The special string "default" indicates that the default format should be used.
tkit.log.json.date-format=default
# The special end-of-record delimiter to be used. By default, no delimiter is used. (optional)
tkit.log.json.record-delimiter=
# The zone ID to use. The special string "default" indicates that the default zone should be used.
tkit.log.json.zone-id=default
# The exception output type to specify.
tkit.log.json.exception-output-type=formatted
# Enable printing of more details in the log.
tkit.log.json.print-details=false
# Add MDC keys mapping. (list)
tkit.log.json.keys.mdc=
# Add MDC prefix mapping. (list)
tkit.log.json.keys.group=
# Ignore keys. (list)
tkit.log.json.keys.ignore=
# Override keys. (list)
tkit.log.json.keys.override=
# Type of the keys. (list)
tkit.log.json.keys.type=
# Environment keys. (list)
tkit.log.json.keys.env=
# Number of characters after which the stacktrace is split. We produce linked messages.
tkit.log.json.split-stacktrace-after=12000
```

