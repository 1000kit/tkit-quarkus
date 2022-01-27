package org.tkit.quarkus.jpa.test;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "TEST_USER")
public class User extends TraceableEntity {

    private String name;

    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ADDRESS_GUID")
    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

