# tkit-quarkus-jpa

1000kit Quarkus JPA database models extension

Maven dependency
```xml
<dependency>
    <groupId>org.tkit.quarkus.lib</groupId>
    <artifactId>tkit-quarkus-jpa-models</artifactId>
</dependency>
```

## Documentation

This extension contains abstract classes for the `JPA Entity`

We have these abstract classes for `Entity`
* `org.tkit.quarkus.jpa.models.TraceableEntity` - base `Entity` abstract class which implements
  traceable fields `creationUser`,`creationDate`,`modificationDate` and `modificationUser`. The type of the `ID` field is `String`.
  The `ID` is generated when you create java instance with `UUID.randomUUID().toString()`

In the project you need to extend `Entities` from one of these abstract classes.

## Business ID

For the `business ID` use corresponding pattern. The primary ID is `GUID` from the `TraceableEntity`.

### PostgreSQL SERIAL type
```java
@Entity
@Table(name = "BUSINESS_PROJECT")
public class BusinessProject extends TraceableEntity {

    @Generated(GenerationTime.INSERT)
    @Column(name = "bid", columnDefinition = "SERIAL")
    private Long bid;

    public Long getBid() { return bid; }
    public void setBid(Long bid) { this.bid = bid; }
}
```
### Generic sequence implementation.
```java
import jakarta.persistence.Column;

import org.my.group.BusinessId;

@BusinessId(sequence = "SEQ_MY_ENTITY_BID")
@Column(name = "bid_anno")
Long bid;
```
Follow the implementation of the custom annotation `org.my.group.BusinessId`.
```java
package org.my.group;

import org.hibernate.annotations.ValueGenerationType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@ValueGenerationType(generatedBy = BusinessIdValueGeneration.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessId {

    String sequence() default "";
    
}
```
Follow the implementation of the custom generator which we use in the annotation.
```java
public class BusinessIdGenerator implements ValueGenerator<Long> {

    String sequence;

    BusinessIdGenerator(String sequence) {
        this.sequence = sequence;
    }

    @Override
    public Long generateValue(Session session, Object owner) {
        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) session.getSessionFactory();
        String sql = sessionFactory.getJdbcServices().getDialect().getSequenceNextValString(sequence);
        ReturningWork<Long> seq = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery();
            ) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        };
        return sessionFactory.getCurrentSession().doReturningWork(seq);
    }
}
```
We need to add the sequence to the import.sql for the local development.
```sql
CREATE SEQUENCE IF NOT EXISTS seq_my_entity_bid;
```
### Custom in-Memory generator
```java
@GeneratorType(type = BidStringGenerator.class, when = GenerationTime.INSERT)
@Column(name = "CUSTOM")
String custom;
```
Follow the implementation of the custom generator `org.my.group.BidStringGenerator`.
```java
public class BidStringGenerator implements ValueGenerator<String> {

    @Override
    public String generateValue(Session session, Object owner) {
        MyEntity e = (MyEntity) owner;
        return e.name + "+" + UUID.randomUUID();
    }
}
```