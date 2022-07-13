
# 1000kit Quarkus extension

[![License](https://img.shields.io/github/license/quarkusio/quarkus?style=for-the-badge&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)
[![Quarkus version](https://img.shields.io/maven-central/v/io.quarkus/quarkus-bom?logo=apache-maven&style=for-the-badge&label=Quarkus)](https://search.maven.org/artifact/io.quarkus/quarkus-bom)
[![Supported JVM Versions](https://img.shields.io/badge/JVM-17-brightgreen.svg?style=for-the-badge&logo=Java)](https://openjdk.org/projects/jdk/17/)

# Extensions

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

## Context
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-context</artifactId>
</dependency>
```
#### Configuration
```properties

```

## JPA

### JPA models
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-jpa-models</artifactId>
</dependency>
```
#### Configuration
```properties

```

### JPA DAO
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-jpa</artifactId>
</dependency>
```

#### Configuration
```properties

```

## Log
Quarkus common log extension
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-log-cdi</artifactId>
</dependency>
```
#### Configuration
```properties

```

### JSON

JSON log extension
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-log-json</artifactId>
</dependency>
```
#### Configuration
```properties

```

### Rest
Log extension for the rest-service.
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-log-rs</artifactId>
</dependency>
```
#### Configuration
```properties

```

## Rest

```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-rest</artifactId>
</dependency>
```
#### Configuration
```properties

```

## Database import for test
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-test-db-import</artifactId>
    <scope>test</scope>
</dependency>
```
#### Configuration
```properties

```

