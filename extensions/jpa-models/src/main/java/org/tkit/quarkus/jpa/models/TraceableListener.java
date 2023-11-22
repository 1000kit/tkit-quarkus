package org.tkit.quarkus.jpa.models;

import java.io.Serializable;
import java.security.Principal;
import java.time.LocalDateTime;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * @author msomora
 */
@RegisterForReflection
public class TraceableListener implements Serializable {

    /**
     * Marks the entity as created.
     *
     * @param entity the traceable persistent entity.
     */
    @PrePersist
    public void prePersist(AbstractTraceableEntity<?> entity) {
        if (!entity.isControlTraceabilityManual()) {
            String user = getPrincipal();
            if (user != null) {
                entity.setCreationUser(user);
                entity.setModificationUser(user);
            }
            LocalDateTime date = LocalDateTime.now();
            entity.setCreationDate(date);
            entity.setModificationDate(date);
        }
    }

    /**
     * Marks the entity as changed.
     *
     * @param entity the traceable persistent entity.
     */
    @PreUpdate
    public void preUpdate(AbstractTraceableEntity<?> entity) {
        if (!entity.isControlTraceabilityManual()) {
            String user = getPrincipal();
            if (user != null) {
                entity.setModificationUser(user);
            }
            entity.setModificationDate(LocalDateTime.now());
        }
    }

    private String getPrincipal() {
        final Context context = ApplicationContext.get();
        if (context != null) {
            return context.getPrincipal();
        }

        Instance<Principal> principalInstance = CDI.current().select(Principal.class);
        if (principalInstance.isResolvable()) {
            return principalInstance.get().getName();
        }

        return null;
    }
}
