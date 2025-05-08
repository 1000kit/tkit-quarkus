package org.tkit.quarkus.jpa.test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import org.tkit.quarkus.jpa.models.TraceableEntity;

@Entity
@Table(name = "BUSINESS_PROJECT")
public class BusinessProject extends TraceableEntity {

    @Generated(event = EventType.INSERT)
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
