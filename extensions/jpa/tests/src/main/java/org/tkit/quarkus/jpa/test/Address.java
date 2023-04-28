package org.tkit.quarkus.jpa.test;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.tkit.quarkus.jpa.models.TraceableEntity;

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
