package org.tkit.quarkus.jpa.test;

import static org.hibernate.generator.EventType.INSERT;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.Generated;
import org.tkit.quarkus.jpa.models.TraceableEntity;

@Entity
@Table(name = "BUSINESS_PROJECT")
@SuppressWarnings("java:S2160")
public class BusinessProject extends TraceableEntity {

    @Generated(event = INSERT)
    @Column(name = "bid", columnDefinition = "SERIAL")
    private Long bid;

    private String name;

    public Long getBid() {
        return bid;
    }

    public void setBid(Long bid) {
        this.bid = bid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
