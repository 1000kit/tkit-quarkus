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

import org.tkit.quarkus.log.cdi.LogParamValue;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Logger builder service.
 */
@Singleton
public class LogParamValueService {

    /**
     * Gets the map of the mapping function for the class.
     */
    public static Map<Class<?>, Function<Object, String>> CLASSES = new ConcurrentHashMap<>(JavaTypesLogParamValue.classes());

    /**
     * Gets the map of the mapping function for the assignable class.
     */
    public static Map<Class<?>, Function<Object, String>> ASSIGNABLE_FROM = new HashMap<>(JavaTypesLogParamValue.assignableFrom());

    @Inject @Any
    Instance<LogParamValue> services;

    public void init() {
        if (services != null) {
            Map<Class<?>, LogParamValue.Item> classes = new HashMap<>();
            Map<Class<?>, LogParamValue.Item> assignable = new HashMap<>();
            for (LogParamValue def : services) {
                map(def.getClasses(), classes);
                map(def.getAssignableFrom(), assignable);
            }
            classes.forEach((c, item) -> CLASSES.put(c, item.fn));
            assignable.forEach((c, item) -> ASSIGNABLE_FROM.put(c, item.fn));
        }
    }

    private void map(List<LogParamValue.Item> items, Map<Class<?>, LogParamValue.Item> target) {
        if (items != null) {
            for (LogParamValue.Item item : items) {
                LogParamValue.Item tmp = target.get(item.clazz);
                if (tmp != null) {
                    if (item.priority > tmp.priority) {
                        target.put(item.clazz, item);
                    }
                } else {
                    target.put(item.clazz, item);
                }
            }
        }
    }

    /**
     * Gets the method parameter value.
     *
     * @param parameter the method parameter.
     * @return the value from the parameter.
     */
    public String getParameterValue(Object parameter) {
        if (parameter != null) {
            Class<?> clazz = parameter.getClass();
            Function<Object, String> fn = CLASSES.get(clazz);
            if (fn != null) {
                return fn.apply(parameter);
            }

            for (Map.Entry<Class<?>, Function<Object, String>> entry : ASSIGNABLE_FROM.entrySet()) {
                if (entry.getKey().isAssignableFrom(clazz)) {
                    Function<Object, String> fn2 = entry.getValue();
                    CLASSES.put(clazz, fn2);
                    return fn2.apply(parameter);
                }
            }
        }
        return "" + parameter;
    }


}
