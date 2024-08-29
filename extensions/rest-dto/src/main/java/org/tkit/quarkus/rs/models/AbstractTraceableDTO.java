package org.tkit.quarkus.rs.models;

import java.io.Serializable;
import java.time.OffsetDateTime;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * The persistent entity interface.
 *
 * @deprecated will be removed in next major release.
 */
@Deprecated(since = "1.0.0")
@RegisterForReflection
@SuppressWarnings("java:S1133")
public abstract class AbstractTraceableDTO<T> implements Serializable {

    /**
     * The UID of this class.
     */
    private static final long serialVersionUID = -8041083748062531412L;

    /**
     * Optimistic lock version
     */
    private Integer modificationCount;

    /**
     * The creation date.
     */
    private OffsetDateTime creationDate;
    /**
     * The creation user.
     */
    private String creationUser;
    /**
     * The modification date.
     */
    private OffsetDateTime modificationDate;
    /**
     * The modification user.
     */
    private String modificationUser;

    /**
     * Gets the GUID.
     *
     * @return the GUID.
     */
    public abstract T getId();

    /**
     * Sets the GUID.
     *
     * @param guid the new GUID.
     */
    public abstract void setId(T guid);

    /**
     * Overwrite the {@code toString} method for the logger.
     *
     * @return the className:Id
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + getId();
    }

    /**
     * Gets the optimistic lock version/modification counter value.
     *
     * @return the optimistic lock version.
     */
    public Integer getModificationCount() {
        return this.modificationCount;
    }

    /**
     * Sets the opt lock version/modification counter. Should not be normally used by app programers.
     *
     * @param modificationCount actual Version
     */
    public void setModificationCount(Integer modificationCount) {
        this.modificationCount = modificationCount;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationUser() {
        return creationUser;
    }

    public void setCreationUser(String creationUser) {
        this.creationUser = creationUser;
    }

    public OffsetDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(OffsetDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getModificationUser() {
        return modificationUser;
    }

    public void setModificationUser(String modificationUser) {
        this.modificationUser = modificationUser;
    }
}
