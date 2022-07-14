# tkit-quarkus-data-import

1000kit quarkus data import extension during the start of the application.

Maven dependency
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-data-import</artifactId>
</dependency>
```

Configuration
```properties
# enable or disable the data import. Default `true`
quarkus.tkit.dataimport.enabled=true|false 
# file to import
quarkus.tkit.dataimport.configurations.<KEY>.file=
# bean key defined in the @DataImport annotation. Default empty and <KEY> 
# value will be used for mapping the configuration to the bean.
quarkus.tkit.dataimport.configurations.<KEY>.bean=
# metadata string-string map
quarkus.tkit.dataimport.configurations.<KEY>.metadata=
# enable or disable the data import for the key. Default `true`
quarkus.tkit.dataimport.configurations.<KEY>.enabled=true|false
# stop at error. Default `false` 
quarkus.tkit.dataimport.configurations.<KEY>.stop-at-error=true|false
```


Example configuration
```properties
quarkus.tkit.dataimport.enabled=true
quarkus.tkit.dataimport.configurations.key1.file=/data/example_data.json
quarkus.tkit.dataimport.configurations.key1.metadata.operation=UPDATE
quarkus.tkit.dataimport.configurations.key1.metadata.check=FALSE
quarkus.tkit.dataimport.configurations.key1.enabled=true
quarkus.tkit.dataimport.configurations.key1.stop-at-error=false
```

The log of the data import changes is stored in the `dataimportlog` table. To create this table copy and paste these liquibase changes to your project.
```xml
<?xml version="1.1" encoding="UTF-8" standalone="no"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"
                   xmlns:pro="http://www.liquibase.org/xml/ns/pro"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd http://www.liquibase.org/xml/ns/pro http://www.liquibase.org/xml/ns/pro/liquibase-pro-3.8.xsd http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd"
                   objectQuotingStrategy="QUOTE_ONLY_RESERVED_WORDS">
    <changeSet author="dev (generated)" id="dataimportlog-1">
        <createTable tableName="dataimportlog">
            <column name="id" type="VARCHAR(255)">
                <constraints nullable="false" primaryKey="true" primaryKeyName="dataimportlog_pkey"/>
            </column>
            <column name="creationdate" type="TIMESTAMP WITHOUT TIME ZONE"/>
            <column name="modificationdate" type="TIMESTAMP WITHOUT TIME ZONE"/>
            <column name="file" type="VARCHAR(255)"/>
            <column name="md5" type="VARCHAR(255)"/>
            <column name="error" type="VARCHAR(255)"/>
        </createTable>
    </changeSet>
</databaseChangeLog>
```
Or embedded it in the project from file [create-log-table](runtime/src/main/resources/db/changelog/create-log-table.xml)
```xml
<include file="classpath:/db/create-log-table.xml"/>
```
Example [changelog.xml](tests/src/main/resources/db/changeLog.xml)

Flyway sql script:
```sql
CREATE TABLE DATAIMPORTLOG (
    ID VARCHAR(255) NOT NULL,
    CREATIONDATE TIMESTAMP WITHOUT TIME ZONE,
    MODIFICATIONDATE TIMESTAMP WITHOUT TIME ZONE,
    FILE VARCHAR(255),
    MD5 VARCHAR(255),
    ERROR VARCHAR(255),
    CONSTRAINT DATAIMPORTLOG_PKEY PRIMARY KEY (ID)
);
```

Create bean with `@DataImport("my-import-key")` bean which implements the `DataImportService` interface.
Example bean: [ParameterTestImport](tests/src/main/java/org/tkit/quarkus/dataimport/test/ParameterTestImport.java)
```java
@DataImport("my-import-key")
public class ParameterTestImport implements DataImportService {

    @Inject MyModelDAO dao;
    @Inject ObjectMapper mapper;

    @Override
    public void importData(DataImportConfig config) {
        try {
            MyModel model = mapper.readValue(config.getData(), MyModel.class);
            dao.create(model);
        } catch (Exception ex) {
            throw new RuntimeException("Error import", ex);
        }
    }
}
```