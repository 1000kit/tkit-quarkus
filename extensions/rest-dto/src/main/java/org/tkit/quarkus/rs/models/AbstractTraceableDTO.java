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

import java.io.Serializable;
import java.time.OffsetDateTime;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * The persistent entity interface.
 */
@Deprecated
@RegisterForReflection
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
