package org.tkit.quarkus.jpa.test;

import org.tkit.quarkus.jpa.models.AbstractTraceableEntity;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

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
