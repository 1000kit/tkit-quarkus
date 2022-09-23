
# 1000kit Quarkus extension

[![License](https://img.shields.io/github/license/quarkusio/quarkus?style=for-the-badge&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)
[![Release version](https://img.shields.io/maven-central/v/org.tkit.quarkus.lib/tkit-quarkus-bom?logo=apache-maven&style=for-the-badge&label=Release)](https://search.maven.org/artifact/org.tkit.quarkus.lib/tkit-quarkus-bom)
[![Quarkus version](https://img.shields.io/maven-central/v/io.quarkus/quarkus-bom?logo=apache-maven&style=for-the-badge&label=Quarkus)](https://search.maven.org/artifact/io.quarkus/quarkus-bom)
[![Supported JVM Versions](https://img.shields.io/badge/JVM-17-brightgreen.svg?style=for-the-badge&logo=Java)](https://openjdk.org/projects/jdk/17/)
[![GitHub Actions Status](https://img.shields.io/github/workflow/status/1000kit/tkit-quarkus/build?logo=GitHub&style=for-the-badge)](https://github.com/1000kit/tkit-quarkus/actions/workflows/build.yml)
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=for-the-badge)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->
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

| Name              | ArtifactId                   | Info                                                               | Description                                                                                                                                                                                                                                                         | Documentation                               |
|-------------------|------------------------------|--------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------|
| Context           | tkit-quarkus-context         |                                                                    | Context propagation helpers used by other extensions like logging or jpa.                                                                                                                                                                                           | [Link](extensions/context/README.md)        |
| Data Import       | tkit-quarkus-data-import     |                                                                    | Extension for business data import - initial data, mounted import files etc.                                                                                                                                                                                        | [Link](extensions/data-import/README.md)    |
| JPA models        | tkit-quarkus-jpa-models      |                                                                    | Base classes for JPA entities, traceable superclass and related classes.                                                                                                                                                                                            | [Link](extensions/jpa-models/README.md)     |
| JPA DAO           | tkit-quarkus-jpa             | > Note: Consider using official Quarkus Panache extension instead. | Abstract DAO/Repository superclass and related utilities.                                                                                                                                                                                                           | [Link](extensions/jpa/README.md)            |
| Log CDI           | tkit-quarkus-log-cdi         |                                                                    | Quarkus extension for CDI method logging(business method start/stop logging).                                                                                                                                                                                       | [Link](extensions/log/cdi/README.md)        |
| Log RS            | tkit-quarkus-log-rs          |                                                                    | Quarkus extension for HTTP request logging (client & server).                                                                                                                                                                                                       | [Link](extensions/log/rs/README.md)         |
| Log JSON          | tkit-quarkus-log-json        |                                                                    | Custom JSON log formatter that provides additional features not included in [Official Quarkus json logger](https://quarkus.io/guides/logging#json-logging). Make sure you only include this if you need those extra features, otherwise use the official extension. | [Link](extensions/log/json/README.md)       |
| Log Rest          | tkit-quarkus-rest            |                                                                    | Helper classes for JAX-RS - model mapping, exception handling, DTOs.                                                                                                                                                                                                | [Link](extensions/rest/README.md)           |
| Test data import  | tkit-quarkus-test-db-import  |                                                                    | Test extension for data import from excel into database during unit tests.                                                                                                                                                                                          | [Link](extensions/test-db-import/README.md) |

### Migration from older version

If you have used previous versions of tkit quarkus libraries (mvn groupId `org.tkit.quarkus`) then there are a few breaking changes in this new version, however the migration is straightforward:

1. Quarkus 2.x 
Tkit Quarkus libs only support Quarkus version 2. Check the [official guide](https://github.com/quarkusio/quarkus/wiki/Migration-Guide-2.0) for migration instructions.

2. Change maven imports
Group id of the libraries has changed to `org.tkit.quarkus.lib`. Also, prefer the use of bom import, to ensure version compatibility. So if your current pom.xml looks sth like this:

```xml
<dependencies>
    <dependency>
        <groupId>org.tkit.quarkus</groupId>
        <artifactId>tkit-quarkus-....</artifactId>
    </dependency>
</dependencies>
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
    <!-- Other dependencies -->
</dependencies>
```

3. Update configuration
All extensions and libraries now have unified configuration properties structure, starting with `tkit.` prefix, some keys have been renamed or otherwise updated. Check the table bellow for config property migration:

| Old                               | New                                          | Note                                                                                                                   |
|-----------------------------------|----------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| `quarkus.tkit.log.ignore.pattern` | `tkit.log.cdi.auto-discovery.ignore.pattern` |                                                                                                                        |
| `quarkus.tkit.log.packages`       | `tkit.log.cdi.auto-discovery.packages`       | In order to enable auto binding of logging extension, you must add property `tkit.log.cdi.auto-discovery.enabled=true` |

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

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://www.lorislab.org"><img src="https://avatars2.githubusercontent.com/u/828045?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Andrej Petras</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkiverse-unleash/commits?author=andrejpetras" title="Code">💻</a> <a href="#maintenance-andrejpetras" title="Maintenance">🚧</a></td>
  </tr>  
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification.
Contributions of any kind welcome!

