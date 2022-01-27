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

package org.tkit.quarkus.log.cdi;

import javax.enterprise.util.Nonbinding;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation is for the logger mapping method.
 * The method needs to be public and static. The input parameter muss be object type and result string.
 */
@Target({METHOD})
@Retention(RUNTIME)
@Documented
public @interface LogParam {

    /**
     * The list of classes for which is this mapping method.
     *
     * @return the list of classes for which is this mapping method.
     */
    @Nonbinding Class[] classes() default {};

    /**
     * The list of  assignable classes for which is this mapping method.
     *
     * @return the list of  assignable classes for which is this mapping method.
     */
    @Nonbinding Class[] assignableFrom() default {};

    /**
     * The priority of this method.
     *
     * @return the priority of this method.
     */
    @Nonbinding int priority() default 0;
}
