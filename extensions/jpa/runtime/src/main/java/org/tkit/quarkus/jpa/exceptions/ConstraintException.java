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

import java.io.Serializable;

/**
 * The constraint exception.
 */
public class ConstraintException extends DAOException {

    /**
     * The constraints parameter key.
     */
    private static final String PARAMETER = "constraint";

    private static final String NAME = "constraintName";

    /**
     * The default constructor.
     *
     * @param constraints the constraints message.
     * @param messageKey the message key.
     * @param cause the cause exception.
     * @param params the exception parameters.
     */
    public ConstraintException(String constraints, Enum<?> messageKey, Throwable cause, Serializable... params) {
        super(messageKey, cause, params);
        addParameter(PARAMETER, constraints);
    }

    /**
     * Sets the constraint name.
     *
     * @param name the name of the constraint.
     */
    public void addConstraintName(String name) {
        addParameter(NAME, name);
    }

    /**
     * Gets the constraints name.
     *
     * @return the constraints name.
     */
    public String getConstraintName() {
        return (String) namedParameters.get(NAME);
    }

    /**
     * Gets the constraints message.
     *
     * @return the constraints message.
     */
    public String getConstraints() {
        return (String) namedParameters.get(PARAMETER);
    }

}
