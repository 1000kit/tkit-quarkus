# tkit-quarkus-rest-dto

> tkit-quarkus-rest-dto is deprecated. It will be removed in the next major release.

Helper classes for JAX-RS - model mapping, exception handling, DTOs. 

Maven dependency
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-rest-dto</artifactId>
</dependency>
```

### DTO

All `DTO` classes are in the package `org.tkit.quarkus.rs.models`. These classes have weak reference to the
`Entity` classes and `PageResult` class in the [tkit-quarkus-jpa-models](../jpa-models) extension.
The class [RestExceptionDTO](src/main/java/org/tkit/quarkus/rs/models/RestExceptionDTO.java) is for the 
[RestException](src/main/java/org/tkit/quarkus/rs/exceptions/RestException.java).

### Exception

This extension define the `RestException` which should be used in the `RestController`. This exception support
translation for the `errorCode` with `parameters`. The local is taken from the HTTP request. For default value use
Quarkus configuration value `quarkus.default-locale`.  Exception is in the package `org.tkit.quarkus.rs.exceptions`

### Mappers

In the package `org.tkit.quarkus.rs.mappers` is the `ExceptionMapper` for all `Exception` in the project with priority `DefaultExceptionMapper.PRIORITY`.
You can `extend` and overwrite this `ExceptionMapper`. Use the `@Priority` to overwrite existing registration.

