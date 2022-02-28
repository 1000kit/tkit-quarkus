/*
 * Copyright 2020 1000kit.org.
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
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * The logger service annotation.
 */
@Inherited
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface LogService {

    /**
     * Configuration key.
     * @return rest controller configuration key.
     */
    String configKey() default "";

    /**
     * Log the method or class if the flag is {@code true}.
     *
     * @return the log flag.
     */
    @Nonbinding boolean log() default true;

    /**
     * Log the exception stacktrace if the flag is {@code true}
     *
     * @return the stacktrace flag.
     */
    @Nonbinding boolean stacktrace() default true;

}
