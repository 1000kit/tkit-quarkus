package org.tkit.quarkus.jpa.test;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ADDRESS")
public class Address extends TraceableEntity {

    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
