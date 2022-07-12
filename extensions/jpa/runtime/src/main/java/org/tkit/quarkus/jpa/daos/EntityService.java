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
package org.tkit.quarkus.jpa.daos;

/**
 * The service interface to use in the build phase.
 */
public abstract class EntityService<T> {

    /**
     * The entity class.
     *
     * @return the entity class.
     */
    protected Class<T> getEntityClass() {
        return null;
    }

    /**
     * The entity name.
     *
     * @return the entity name.
     */
    protected String getEntityName() {
        return null;
    }

    /**
     * The entity id attribute name.
     *
     * @return the entity name.
     */
    protected String getIdAttributeName() {
        return null;
    }
}
