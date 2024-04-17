package org.tkit.quarkus.jpa.test;

import static jakarta.persistence.FetchType.LAZY;

import java.util.List;

import jakarta.persistence.*;

import org.tkit.quarkus.jpa.models.TraceableEntity;

@Entity
@NamedEntityGraph(name = "Parent.loadChildren", includeAllAttributes = true)
@Table(name = "TEST_PARENT")
public class Parent extends TraceableEntity {

    @OneToMany(fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "PARENT_GUID")
    private List<Child> children;

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }
}
