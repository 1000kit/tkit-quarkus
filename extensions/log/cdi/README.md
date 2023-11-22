# tkit-quarkus-log-cdi

1000kit Quarkus logger extension, core log library for Quarkus ARC `cdi` implementation.

Maven dependency
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-log-cdi</artifactId>
</dependency>
```
### Configuration

Build time configuration.

```properties
# Enable or disable autodiscovery
tkit.log.cdi.auto-discovery.enabled=false
# Binding includes packages. (list)
tkit.log.cdi.auto-discovery.packages=org.tkit
# Specify ignore pattern. (optional)
tkit.log.cdi.auto-discovery.ignore.pattern=
# Bean annotations to discovery. (list)
tkit.log.cdi.auto-discovery.bean.annotations=jakarta.enterprise.context.ApplicationScoped,jakarta.enterprise.context.Singleton,jakarta.enterprise.context.RequestScoped
```
To activate the auto discovery only for rest-controllers use this configuration

```properties
tkit.log.cdi.auto-discovery.bean.annotations=jakarta.ws.rs.Path
```

Runtime configuration.

```properties
# Enable or disable interceptor
tkit.log.cdi.enabled=true
# Prefix for custom data mdc entries. (optional)
tkit.log.cdi.custom-data.prefix=
# Enable or disable start message
tkit.log.cdi.start.enabled=false
# Start log message template. (%1 - method, %2 - method parameters)
tkit.log.cdi.start.template=%1$s(%2$s) started.
# Enable or disable succeed message
tkit.log.cdi.succeed.enabled=true
# Succeed message template (%1 - method, %2 - parameters, %3 - return value, %4 - time)
tkit.log.cdi.succeed.template=%1$s(%2$s):%3$s [%4$.3fs]
# Enable or disable failed message
tkit.log.cdi.failed.enabled=true
# Failed message template (%1 - method, %2 - parameters, %3 - return value, %4 - time)
tkit.log.cdi.failed.template=%1$s(%2$s) throw %3$s [%4$.3fs]
# Return void method template
tkit.log.cdi.return-void-template=void
# Return void method template
tkit.log.cdi.mdc.errorKey=errorNumber
# Enable or disable service log. (optional)
tkit.log.cdi.service.<fully-qualified-class-name|config-key>.log=
# Enable or disable service stacktrace. (optional)
tkit.log.cdi.service.<fully-qualified-class-name|config-key>.stacktrace=
# Enable or disable service method log. (optional)
tkit.log.cdi.service.<fully-qualified-class-name|config-key>.method.<method-name>.log=
# Enable or disable service method stacktrace. (optional)
tkit.log.cdi.service.<fully-qualified-class-name|config-key>.method.<method-name>.stacktrace=
# Set up the mask string for the return type. (optional)
tkit.log.cdi.service.<fully-qualified-class-name|config-key>.method.<method-name>.returnMask=
# Exclude parameters or mask method parameters. (optional)
tkit.log.cdi.service.<fully-qualified-class-name|config-key>.method.<method-name>.param.<index>=<mask>
```