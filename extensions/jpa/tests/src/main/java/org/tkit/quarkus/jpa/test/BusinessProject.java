package org.tkit.quarkus.jpa.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.tkit.quarkus.jpa.models.TraceableEntity;

@Entity
@Table(name = "BUSINESS_PROJECT")
public class BusinessProject extends TraceableEntity {

    @Generated(GenerationTime.INSERT)
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
