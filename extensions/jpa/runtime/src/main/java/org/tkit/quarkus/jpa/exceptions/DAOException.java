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
package org.tkit.quarkus.jpa.exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The exception class for service exception with localized message.
 */
public class DAOException extends RuntimeException {

    /**
     * The key of resource.
     */
    public final Enum<?> key;

    /**
     * The arguments for the message.
     */
    public final List<Object> parameters = new ArrayList<>();

    /**
     * The name parameters.
     */
    public final Map<String, Object> namedParameters = new HashMap<>();

    /**
     * The constructor with the resource key and cause.
     *
     * @param key the resource key.
     * @param parameters the resource key arguments.
     * @param cause the throw able cause.
     */
    public DAOException(final Enum<?> key, final Throwable cause, Object... parameters) {
        super(cause);
        this.key = key;
        if (parameters != null && parameters.length > 0) {
            this.parameters.addAll(Arrays.asList(parameters));
        }
    }

    /**
     * Overwrite the message "key-simple-name,key,parameters"
     *
     * @return the message "key-simple-name,key-name,parameters,namedParameters"
     */
    @Override
    public String getMessage() {
        return key.getClass().getSimpleName() + ",key:" + key.name() + ",parameters:" + parameters + ",namedParameters:"
                + namedParameters;
    }

    /**
     * Gets the message key.
     *
     * @return the message key.
     */
    public Enum<?> getMessageKey() {
        return key;
    }

    /**
     * Adds the parameter.
     *
     * @param parameter the parameter.
     */
    public final void addParameter(Object parameter) {
        parameters.add(parameter);
    }

    /**
     * Add the list of parameters.
     *
     * @param parameters the list of parameters.
     */
    public final void addParameter(List<Object> parameters) {
        if (parameters != null) {
            this.parameters.addAll(parameters);
        }
    }

    /**
     * Adds the named parameter.
     *
     * @param name the named parameter key.
     * @param parameter the parameter value.
     */
    public final void addParameter(String name, Object parameter) {
        if (name != null) {
            namedParameters.put(name, parameter);
        }
    }

    /**
     * Add the key-value parameters.
     *
     * @param namedParameters the map of named parameters.
     */
    public final void addParameter(Map<String, Object> namedParameters) {
        if (namedParameters != null) {
            this.namedParameters.putAll(namedParameters);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return key.name();
    }
}
