package org.tkit.quarkus.jpa.models;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * The persistent entity with string GUID.
 *
 */
@MappedSuperclass
public class TraceableEntity extends AbstractTraceableEntity<String> {

    /**
     * The UID for this class.
     */
    private static final long serialVersionUID = 3699279519938221976L;

    /**
     * String ID of entity
     */
    @Id
    @Column(name = "GUID")
    private String id = UUID.randomUUID().toString();

    /**
     * Gets the GUID.
     *
     * @return the GUID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the GUID.
     *
     * @param id the new GUID.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc }
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TraceableEntity other = (TraceableEntity) obj;
        Object guid = getId();
        Object otherGuid = other.getId();

        if (guid == null) {
            if (otherGuid != null) {
                return false;
            } else {
                return super.equals(obj);
            }
        } else
            return guid.equals(otherGuid);
    }

    /**
     * {@inheritDoc }
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(getId());
        return result;
    }

    /**
     * Overwrite the {@code toString} method for the logger.
     *
     * @return the className:ID
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + getId();
    }
}
