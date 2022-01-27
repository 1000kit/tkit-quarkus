package org.tkit.quarkus.jpa.models;

import org.tkit.quarkus.context.RequestData;
import org.tkit.quarkus.context.RequestDataContext;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.security.Principal;
import java.time.LocalDateTime;

/**
 * @author msomora
 */
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
        final RequestData requestData = RequestDataContext.get();
        if (requestData != null) {
            return requestData.getPrincipal();
        }
        
        Instance<Principal> principalInstance = CDI.current().select(Principal.class);
        if (principalInstance.isResolvable()) {
            return principalInstance.get().getName();
        }
        
        return null;
    }
}
