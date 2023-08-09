# tkit-quarkus-rest

Helper classes for JAX-RS. 

Maven dependency
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-rest</artifactId>
</dependency>
```

### Jackson

This module registered jackson customize module [RegisterCustomModuleCustomizer](src/main/java/org/tkit/quarkus/rs/jackson/RegisterCustomModuleCustomizer.java) 

### OffsetDateTimeMapper

`OffsetDateTimeMapper` and `OffsetDateTimeParamConverterProvider` are used for correct OffsetDateTime mapping.

