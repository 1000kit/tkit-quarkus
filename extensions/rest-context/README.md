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
# Add token to the context
tkit.rs.context.token-context=true
# Principal token is mandatory
tkit.rs.context.token-mandatory=false
# Principal is mandatory
tkit.rs.context.principal-mandatory=false
# Enable or disable token parser service
tkit.rs.context.token.enabled=true
# Make principal token required
tkit.rs.context.token.required=true
# Token type name for parsing token
tkit.rs.context.token.type=principal-token
# Verify the token or skip token verification
tkit.rs.context.token.verify=false
# Enable or disable the public key location for the verified token.
tkit.rs.context.token.public-key-location.enabled=false
# Token public key location suffix. This property is use only if public-key-location.enabled set to true.
tkit.rs.context.token.public-key-location.suffix=/protocol/openid-connect/certs
# Token header parameter
tkit.rs.context.token.header-param=apm-principal-token

```

Runtime principal name configuration.

```properties
# Enable or disable principal name for the context
tkit.rs.context.principal.name.enabled=true
# Enable or disable custom principal service
tkit.rs.context.principal.name.custom-service-enabled=false
# Enabled or disable `SecurityContext` principal name resolver
tkit.rs.context.principal.name.security-context.enabled=false
# Optional default principal name, default null
tkit.rs.context.principal.name.default=
# Read name from token
tkit.rs.context.principal.name.token-enabled=true
# Token claim name for principal name
tkit.rs.context.principal.name.token-claim-name=sub
# Use header parameter as principal name
tkit.rs.context.principal.name.header-param-enabled=false
# Principal name header for header-param-enabled
tkit.rs.context.principal.name.header-param-name=x-principal-id
```

Runtime principal token configuration.

```properties
# Enabled or disable token resolver for principal name
tkit.rs.context.principal.token.enabled=true
```

Runtime tenant configuration.

```properties
# Enable or disable tenant ID resolver for the context.
tkit.rs.context.tenant-id.enabled=false
# default tenant ID
tkit.rs.context.tenant-id.default=default
# Use token claim to setup tenant ID
tkit.rs.context.tenant-id.token.enabled=false
# Token claim parameter for tenant ID 
tkit.rs.context.tenant-id.token.claim-tenant-param=tenantId
# enable or disable tenant ID from header parameter
tkit.rs.context.tenant-id.header-param-enabled=false
# header parameter of the tenant ID
tkit.rs.context.tenant-id.header-param-name=tenant-id
```

Tenant mock service for testing

```properties
# enable or disable tenant ID mock service
tkit.rs.context.tenant-id.mock.enabled=false
# default tenant ID
tkit.rs.context.tenant-id.mock.default-tenant=default
# mock data orgId - tenantId
tkit.rs.context.tenant-id.mock.data.<org-id>=<tenant-id>
# token claim parameter of organization ID
tkit.rs.context.tenant-id.mock.claim-org-id=orgId
```

Priority of the tenant ID resolver:
* mock service
* custom implementation of th `RestCustomTenantResolver` interface
* header parameter

The application can implement its own tenant resolver by implementing the `RestCustomTenantResolver` interface. The header parameter can be accessed using the input parameter `ContainerRequestContext` or by including the request scope bean `RestContextHeaderContainer`.

For `ApplicationContext`. See [context](../context)