package org.tkit.quarkus.rs.models;

import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * @deprecated will be removed in next major release
 */
@Deprecated(since = "1.0.0")
@RegisterForReflection
@SuppressWarnings("java:S1133")
public class BusinessTraceableDTO extends AbstractTraceableDTO<Long> {

    /**
     * The UID for this class.
     */
    private static final long serialVersionUID = 2102461206948885441L;

    /**
     * The ID.
     */
    private Long id;

    /**
     * {@inheritDoc }
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc }
     *
     * @see Object#equals(Object)
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
        BusinessTraceableDTO other = (BusinessTraceableDTO) obj;
        Object guid = getId();
        Object otherGuid = other.getId();

        if (guid == null) {
            if (otherGuid != null) {
                return false;
            } else {
                return super.equals(obj);
            }
        } else if (!guid.equals(otherGuid)) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc }
     *
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(getId());
        return result;
    }

}
