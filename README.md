
# 1000kit Quarkus extension

[![License](https://img.shields.io/github/license/quarkusio/quarkus?style=for-the-badge&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)
[![Release version](https://img.shields.io/maven-central/v/org.tkit.quarkus.lib/tkit-quarkus-bom?logo=apache-maven&style=for-the-badge&label=Release)](https://search.maven.org/artifact/org.tkit.quarkus.lib/tkit-quarkus-bom)
[![Quarkus version](https://img.shields.io/maven-central/v/io.quarkus/quarkus-bom?logo=apache-maven&style=for-the-badge&label=Quarkus)](https://search.maven.org/artifact/io.quarkus/quarkus-bom)
[![Supported JVM Versions](https://img.shields.io/badge/JVM-17-brightgreen.svg?style=for-the-badge&logo=Java)](https://openjdk.org/projects/jdk/17/)

Set of Quarkus extensions and libraries to speed up development of backend microservices.  

## Getting started


Include the following bom artifact into your pom or parent pom and then pick the components you need. 

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.tkit.quarkus.lib</groupId>
            <artifactId>tkit-quarkus-bom</artifactId>
            <version>${tkit.quarkus.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## Components

Include the component in your project by including the corresponding dependency. 

:information_source: Some component come with additional documentation and configuration - check the 'Documentation' link for particular section.

### Context

Context propagation helpers used by other extensions like logging or jpa.

```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-context</artifactId>
</dependency>
``` 

### Data Import

Extension for business data import - initial data, mounted import files etc.


```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-data-import</artifactId>
</dependency>
``` 

[Documentation](extensions/data-import/README.md) 

### JPA models

Base classes for JPA entities, tracaeble superclass and related classes. 

```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-jpa-models</artifactId>
</dependency>
```

[Documentation](extensions/jpa-models/README.md) 

### JPA DAO

Abstract DAO/Repository superclass and related utilities. 

> Note: Consider using official Quarkus Panache extension instead. 

```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-jpa</artifactId>
</dependency>
```

[Documentation](extensions/jpa/README.md) 

### Log CDI

Quarkus extension for CDI method logging(busines method start/stop logging). 

```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-log-cdi</artifactId>
</dependency>
```

[Documentation](extensions/log/cdi/README.md) 

### Log RS

Quarkus extension for HTTP request logging (client & server).

```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-log-rs</artifactId>
</dependency>
```

[Documentation](extensions/log/rs/README.md) 

### JSON

Custom JSON log formatter that provides additional features not included in [Official Quarkus json logger](https://quarkus.io/guides/logging#json-logging). Make sure you only include this if you need those extra features, otherwise use the official extension.

```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-log-json</artifactId>
</dependency>
```

[Documentation](extensions/log/json/README.md) 

### Rest

Helper classes for JAX-RS - model mapping, exception handling, DTOs. 

```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-rest</artifactId>
</dependency>
```

### Test data import

Test extension for data import from excel into database during unit tests.

```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-test-db-import</artifactId>
    <scope>test</scope>
</dependency>
```

[Documentation](extensions/test-db-import/README.md) 

## Migration from older version

If you have used previous versions of tkit quarkus libraries (mvn groupId `org.tkit.quarkus`) then there are a few breaking changes in this new version, however the migration is straightforward:

1. Quarkus 2.x 
Tkit Quarkus libs only support Quarkus version 2. Check the [official guide](https://github.com/quarkusio/quarkus/wiki/Migration-Guide-2.0) for migration instructions. 
2. Change maven imports

Group id of the libraries has changed to `org.tkit.quarkus.lib`. Also, prefer the use of bom import, to ensure version compatibility. So if your current pom.xml looks sth like this:

```xml

```

Change it to:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.tkit.quarkus.lib</groupId>
            <artifactId>tkit-quarkus-bom</artifactId>
            <version>${tkit.quarkus.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
<dependencies>
    <!-- Other deps -->
</dependencies>
```

3. Update configuration
All extensions and libraries now have unified configuration properties structure, starting with `tkit.` prefix, some keys have been renamed or otherwise updated. Check the table bellow for config property migration:

| Old | New  | Note|
| ---  | ---     | --- |
| `quarkus.tkit.log.ignore.pattern`| `tkit.log.cdi.auto-discovery.ignore.pattern`||
| `quarkus.tkit.log.packages` | `tkit.log.cdi.auto-discovery.packages`| In order to enable auto binding of logging extension, you must add property `tkit.log.cdi.auto-discovery.enabled=true` |

4. Default behavior changes

Logging:  
CDI logging now only logs end of business methods (success or error) to reduce logging verbosity. If you restore the behavior and still log start method invocations, set the property: `tkit.log.cdi.start.enabled=true`

Old behavior: 
```
[com.acme.dom.dao.SomeBean] someMethod(param) started
[com.acme.dom.dao.SomeBean] someMethod(param):SomeResultClass finished [0.035s]
```
[com.acme.dom.dao.SomeBean] (executor-thread-0) someMethod(param) started [0.035s]

New behavior:
```
[com.acme.dom.dao.SomeBean] someMethod(param):SomeResultClass [0.035s]
```

4. Class changes





