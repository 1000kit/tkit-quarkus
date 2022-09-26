# tkit-quarkus-test-db-import

Test extension for data import from excel into database during unit tests.

Maven dependency
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-test-db-import</artifactId>
    <scope>test</scope>
</dependency>
```
### Configuration

```properties
# Enable or disable database import utility for the tests. 
tkit.db-import.enabled=true|false
# Name of the database docker image used in the project. The utility will create a JDBC client to service created with this docker image.
tkit.db-import.db-image-name=docker.io/postgres
```


