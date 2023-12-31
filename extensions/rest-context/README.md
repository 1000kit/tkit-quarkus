# tkit-quarkus-rest-context

1000kit Quarkus rest application context. This library contains rest interceptor with priority `1` that
start [ApplicationContext](../context) with `PrincipalName`, `Tenant-Id`, `Request-Id` and `Business-Context`

Maven dependency
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-rest-context</artifactId>
</dependency>
```

### Configuration

Runtime configuration.

```properties
# Enable or disable interceptor
tkit.rs.context.enabled=true
# Enable or disable correlation ID from header
tkit.rs.context.correlation-id.enabled=true
# Correlation ID header parameter
tkit.rs.context.correlation-id.header-param-name=X-Correlation-ID
# Enable or disable business context from header
tkit.rs.context.business-context.enabled=false
# Business context header parameter
tkit.rs.context.business-context.header-param-name=business-context
```

Runtime principal configuration.

```properties
# Enable or disable principal name for the context
tkit.rs.context.principal.enabled=true
# Enabled or disable `SecurityContext` principal name resolver
tkit.rs.context.principal.security-context.enabled=false
# Enabled or disable token resolver for principal name
tkit.rs.context.principal.token.enabled=true
# Verify the token or skip token verification
tkit.rs.context.principal.token.verify=false
# Enable or disable the public key location for the verified token.
tkit.rs.context.principal.token.public-key-location.enabled=false
# Token public key location suffix. This property is use only if public-key-location.enabled set to true.
tkit.rs.context.principal.token.public-key-location.suffix=/protocol/openid-connect/certs
# Token header parameter
tkit.rs.context.principal.token.token-header-param=apm-principal-token
# Token claim name for principal name
tkit.rs.context.principal.token.claim-name=sub
```

Runtime tenant configuration.

```properties
# Enable or disable tenant ID resolver for the context.
tkit.rs.context.tenant-id.enabled=false
# default tenant ID
tkit.rs.context.tenant-id.default=default
# enable or disable tenant ID from header parameter
tkit.rs.context.tenant-id.header-param-enabled=false
# header parameter of the tenant ID
tkit.rs.context.tenant-id.header-param-name=tenant-id
# enable or disable tenant ID mock service
tkit.rs.context.tenant-id.mock.enabled=false
# default tenant ID
tkit.rs.context.tenant-id.mock.default-tenant=default
# mock data orgId - tenantId
tkit.rs.context.tenant-id.mock.data.<org-id>=<tenant-id>
# token claim parameter of organization ID
tkit.rs.context.tenant-id.mock.claim-org-id=orgId
# token header parameter
tkit.rs.context.tenant-id.mock.token-header-param=apm-principal-token
```

Priority of the tenant ID resolver:
* mock service
* custom implementation of th `RestCustomTenantResolver` interface
* header parameter

The application can implement its own tenant resolver by implementing the `RestCustomTenantResolver` interface. The header parameter can be accessed using the input parameter `ContainerRequestContext` or by including the request scope bean `RestContextHeaderContainer`.

For `ApplicationContext`. See [context](../context)