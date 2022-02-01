/*
 * Copyright 2019 1000kit.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tkit.quarkus.jpa.models;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * Abstract base class for entities with businessId primary key. Entity class which is extending this abstract class must define
 * SequenceGenerator. As guid is no more primary key you must create index for it.
 * 
 * <pre>
 * {@code
 *  {@literal @}Table(name = "TABLE_NAME", indexes = {@literal @}Index(name = "TABLE_NAME_GUID_IDX", columnList = "GUID", unique = true))
 *  {@literal @}SequenceGenerator(name = "GEN_TABLE_NAME", sequenceName = "SEQ_TABLE_NAME_BID", allocationSize = 1, initialValue = 1)
 * }
 * </pre>
 *
 *
 * @deprecated use {@link TraceableEntity}
 */
@Deprecated(forRemoval = true, since = "2.8.0")
@MappedSuperclass
public class BusinessTraceableEntity extends AbstractTraceableEntity<Long> {

    /**
     * The UID for this class.
     */
    private static final long serialVersionUID = 2102461206948885441L;

    @Id
    @Column(name = "BID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GEN_BUSINESS_ID")
    private Long id;

    /**
     * Gets the businessId.
     *
     * @return the businessId.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the GUID.
     *
     * @param businessId the new GUID.
     */
    public void setId(Long businessId) {
        this.id = businessId;
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
        BusinessTraceableEntity other = (BusinessTraceableEntity) obj;
        Object guid = getId();
        Object otherGuid = other.getId();

        if (guid == null) {
            if (otherGuid != null) {
                return false;
            } else {
                return super.equals(obj);
            }
        } else return guid.equals(otherGuid);
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
     * @return the className:ID
     */
    @Override
    public String toString() {
        return BusinessTraceableEntity.class.getSimpleName() + ":" + getId();
    }

}
