package org.tkit.quarkus.dataimport.tests;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tkit.quarkus.jpa.models.TraceableEntity;

@Entity
@ApplicationScoped
@Table(name = "USER_TABLE")
public class User extends TraceableEntity {

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
