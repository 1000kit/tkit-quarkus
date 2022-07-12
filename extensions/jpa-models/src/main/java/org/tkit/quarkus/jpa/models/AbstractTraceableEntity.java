package org.tkit.quarkus.jpa.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.*;

/**
 * The persistent entity interface.
 */
@MappedSuperclass
@EntityListeners(TraceableListener.class)
public abstract class AbstractTraceableEntity<T> implements Serializable {

    /**
     * The UID of this class.
     */
    private static final long serialVersionUID = -8041083748062531412L;

    /**
     * Optimistic lock version
     */
    @Version
    @Column(name = "OPTLOCK", nullable = false)
    private Integer version;

    /**
     * The creation date.
     */
    private LocalDateTime creationDate;
    /**
     * The creation user.
     */
    private String creationUser;
    /**
     * The modification date.
     */
    private LocalDateTime modificationDate;
    /**
     * The modification user.
     */
    private String modificationUser;

    /**
     * The persisted flag.
     */
    @Transient
    protected boolean persisted;

    /**
     * The modification user data.
     */
    @Transient
    private boolean controlTraceabilityManual = false;

    /**
     * {@inheritDoc}
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * {@inheritDoc}
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * {@inheritDoc}
     */
    public String getCreationUser() {
        return creationUser;
    }

    /**
     * {@inheritDoc}
     */
    public void setCreationUser(String creationUser) {
        this.creationUser = creationUser;
    }

    /**
     * {@inheritDoc}
     */
    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    /**
     * {@inheritDoc}
     */
    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    /**
     * {@inheritDoc}
     */
    public String getModificationUser() {
        return modificationUser;
    }

    /**
     * {@inheritDoc}
     */
    public void setModificationUser(String modificationUser) {
        this.modificationUser = modificationUser;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isControlTraceabilityManual() {
        return controlTraceabilityManual;
    }

    /**
     * {@inheritDoc}
     */
    public void setControlTraceabilityManual(boolean controlTraceabilityManual) {
        this.controlTraceabilityManual = controlTraceabilityManual;
    }

    /**
     * Gets the optimistic lock version.
     *
     * @return the optimistic lock version.
     */
    public Integer getVersion() {
        return this.version;
    }

    /**
     * Sets the version.
     *
     * @param version actual Version
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * The entity life-cycle method.
     */
    @PostLoad
    @PostUpdate
    @PostPersist
    public void checkPersistentState() {
        this.persisted = true;
    }

    /**
     * Gets the {@code true} if the entity is persisted.
     *
     * @return {@code true} if the entity is persisted.
     */
    public boolean isPersisted() {
        return persisted;
    }

    /**
     * Sets the persisted flag.
     *
     * @param persisted the persisted flag.
     */
    public void setPersisted(boolean persisted) {
        this.persisted = persisted;
    }

}
