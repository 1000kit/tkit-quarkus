package org.tkit.quarkus.jpa.test;

import org.tkit.quarkus.jpa.models.AbstractTraceableEntity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
public class TestProject extends AbstractTraceableEntity<String> {

    @Id
    private String key = UUID.randomUUID().toString();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
