package org.tkit.quarkus.it.jpa.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

@Entity
@Table(name = "T_USER")
public class User extends TraceableEntity {

    @TenantId
    @Column(name = "tenant_id")
    public String tenantId;

    @Column(name = "username")
    public String username;

    @Column(name = "email")
    public String email;
}
