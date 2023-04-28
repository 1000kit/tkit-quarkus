package org.tkit.quarkus.jpa.test;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "PROJECT")
public class Project extends TestProject {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
