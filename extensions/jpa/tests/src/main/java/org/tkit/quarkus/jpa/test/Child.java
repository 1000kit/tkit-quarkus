package org.tkit.quarkus.jpa.test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.tkit.quarkus.jpa.models.TraceableEntity;

@Entity
@Table(name = "TEST_CHILD")
public class Child extends TraceableEntity {
    @Column(name = "MFE_ID", nullable = false)
    private String mfeId;

    public String getMfeId() {
        return mfeId;
    }

    public void setMfeId(String mfeId) {
        this.mfeId = mfeId;
    }
}
