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
package org.tkit.quarkus.log.cdi.interceptor;

import org.jboss.logging.MDC;

/**
 * The interceptor context.
 */
public class InterceptorContext {

    /**
     * The start time.
     */
    private final long startTime;

    /**
     * The service method.
     */
    public final String method;

    /**
     * The list of method parameters.
     */
    public final String parameters;

    /**
     * The result value.
     */
    public String result;

    /**
     * The execution time.
     */
    public String time;

    /**
     * The default constructor.
     * @param method the method.
     * @param parameters the method parameters.
     */
    public InterceptorContext(String method, String parameters) {
        this.startTime = System.currentTimeMillis();
        this.method = method;
        this.parameters = parameters;
    }

    /**
     * Close the context.
     * @param result the result.
     */
    public void closeContext(String result) {
        time = String.format("%.3f", (System.currentTimeMillis() - startTime) / 1000f);
        this.result = result;
    }

}
