/*
 * Copyright 2020 tkit.org.
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
package org.tkit.quarkus.rs.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

@RegisterForReflection
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
