package org.tkit.quarkus.jpa.test;

import java.util.UUID;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.tkit.quarkus.jpa.models.AbstractTraceableEntity;

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
