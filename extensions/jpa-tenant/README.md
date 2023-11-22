# tkit-quarkus-jpa-tenant

1000kit Quarkus hibernate tenant resolver.

Maven dependency
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-jpa-tenant</artifactId>
</dependency>
```

### Configuration

Runtime configuration.

```properties
# Default tenant for the hibernate tenant resolver.
tkit.jpa.tenant.default=default
```

This will be not use if tenant is set in the `ApplicationContext`. See [rest-context](../rest-context)