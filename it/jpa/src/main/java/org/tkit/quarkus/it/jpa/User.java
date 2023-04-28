package org.tkit.quarkus.it.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.tkit.quarkus.jpa.models.TraceableEntity;

@Entity
@Table(name = "T_USER")
public class User extends TraceableEntity {

    @Column(name = "username")
    public String username;

    @Column(name = "email")
    public String email;
}
