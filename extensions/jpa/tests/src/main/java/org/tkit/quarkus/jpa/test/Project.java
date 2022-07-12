package org.tkit.quarkus.jpa.test;

import javax.persistence.Entity;
import javax.persistence.Table;

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
