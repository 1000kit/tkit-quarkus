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

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class JavaTypesLogParamValue {


    public static Map<Class<?>, Function<Object, String>> assignableFrom() {
        Map<Class<?>, Function<Object, String>> result = new HashMap<>();
        result.put(Collection.class, JavaTypesLogParamValue::collection);
        result.put(InputStream.class, JavaTypesLogParamValue::inputStream);
        result.put(Map.class, JavaTypesLogParamValue::map);
        result.put(OutputStream.class, JavaTypesLogParamValue::outputStream);
        result.put(Stream.class, JavaTypesLogParamValue::stream);
        return result;
    }

    public static Map<Class<?>, Function<Object, String>> classes() {
        Map<Class<?>, Function<Object, String>> result = new HashMap<>();
        result.put(Class[].class, JavaTypesLogParamValue::array);
        result.put(int[].class, JavaTypesLogParamValue::array);
        result.put(double[].class, JavaTypesLogParamValue::array);
        result.put(float[].class, JavaTypesLogParamValue::array);
        result.put(boolean[].class, JavaTypesLogParamValue::array);
        result.put(long[].class, JavaTypesLogParamValue::array);
        result.put(byte[].class, JavaTypesLogParamValue::array);
        result.put(Integer[].class, JavaTypesLogParamValue::array);
        result.put(Double[].class, JavaTypesLogParamValue::array);
        result.put(String[].class, JavaTypesLogParamValue::array);
        result.put(Boolean[].class, JavaTypesLogParamValue::array);
        result.put(Long[].class, JavaTypesLogParamValue::array);
        result.put(Byte[].class, JavaTypesLogParamValue::array);
        result.put(Class.class, JavaTypesLogParamValue::basic);
        result.put(byte.class, JavaTypesLogParamValue::basic);
        result.put(int.class, JavaTypesLogParamValue::basic);
        result.put(double.class, JavaTypesLogParamValue::basic);
        result.put(float.class, JavaTypesLogParamValue::basic);
        result.put(boolean.class, JavaTypesLogParamValue::basic);
        result.put(long.class, JavaTypesLogParamValue::basic);
        result.put(Integer.class, JavaTypesLogParamValue::basic);
        result.put(Double.class, JavaTypesLogParamValue::basic);
        result.put(String.class, JavaTypesLogParamValue::basic);
        result.put(Boolean.class, JavaTypesLogParamValue::basic);
        result.put(Long.class, JavaTypesLogParamValue::basic);
        result.put(Byte.class, JavaTypesLogParamValue::basic);
        result.put(Enum.class, JavaTypesLogParamValue::enumeration);
        result.put(HashMap.class, JavaTypesLogParamValue::map);
        result.put(HashSet.class, JavaTypesLogParamValue::collection);
        result.put(ArrayList.class, JavaTypesLogParamValue::collection);
        return result;
    }

    /**
     * Mapping method for the basic.
     *
     * @param parameter to map.
     * @return the corresponding string result for the log.
     */
    public static String basic(Object parameter) {
        return "" + parameter;
    }

    /**
     * Mapping method for the array.
     *
     * @param parameter to map.
     * @return the corresponding string result for the log.
     */
    public static String array(Object parameter) {
        return parameter.getClass().getSimpleName() + "[" + Array.getLength(parameter) + "]";
    }

    /**
     * Mapping method for the enumeration.
     *
     * @param parameter to map.
     * @return the corresponding string result for the log.
     */
    public static String enumeration(Object parameter) {
        return parameter.getClass().getSimpleName() + ":" + parameter.toString();
    }

    /**
     * Mapping method for the input-stream.
     *
     * @param parameter to map.
     * @return the corresponding string result for the log.
     */
    public static String inputStream(Object parameter) {
        return parameter.getClass().getSimpleName();
    }

    /**
     * Mapping method for the output-stream.
     *
     * @param parameter to map.
     * @return the corresponding string result for the log.
     */
    public static String outputStream(Object parameter) {
        return parameter.getClass().getSimpleName();
    }

    /**
     * Mapping method for the map.
     *
     * @param parameter to map.
     * @return the corresponding string result for the log.
     */
    public static String map(Object parameter) {
        StringBuilder sb = new StringBuilder();

        String name = parameter.getClass().getSimpleName();
        Map<?, ?> tmp = (Map<?, ?>) parameter;

        if (tmp.isEmpty()) {
            sb.append("empty ").append(name);
        } else {
            sb.append(name).append(' ').append(tmp.size()).append(" of [");

            String keyClassName = null;
            String valueClassName = null;

            if (parameter.getClass().getGenericSuperclass() instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) parameter.getClass().getGenericSuperclass();

                Type keyType = parameterizedType.getActualTypeArguments()[0];
                if (!(keyType instanceof TypeVariable)) {
                    keyClassName = keyType.getClass().getSimpleName();
                }

                Type valueType = parameterizedType.getActualTypeArguments()[1];
                if (!(valueType instanceof TypeVariable)) {
                    keyClassName = valueType.getClass().getSimpleName();
                }
            }

            Map.Entry<?, ?> item = tmp.entrySet().iterator().next();
            // get key class name
            if (keyClassName == null && item.getKey() != null) {
                keyClassName = item.getKey().getClass().getSimpleName();
            }
            // get value class name
            if (item.getValue() != null) {
                valueClassName = item.getValue().getClass().getSimpleName();
            }
            sb.append(keyClassName).append('+').append(valueClassName).append(']');
        }
        return sb.toString();
    }

    /**
     * Mapping method for the collection.
     *
     * @param parameter to map.
     * @return the corresponding string result for the log.
     */
    public static String collection(Object parameter) {
        Collection<?> tmp = (Collection<?>) parameter;
        String name = tmp.getClass().getSimpleName();

        StringBuilder sb = new StringBuilder();

        if (tmp.isEmpty()) {
            sb.append("empty ").append(name);
        } else {
            sb.append(name).append('(').append(tmp.size());
            Class clazz = Object.class;
            if (parameter.getClass().getGenericSuperclass() instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) parameter.getClass().getGenericSuperclass();
                Type type = parameterizedType.getActualTypeArguments()[0];
                clazz = type.getClass();

                // check generic type variable
                if (type instanceof TypeVariable) {

                    // load first item from the collection
                    Object obj = tmp.iterator().next();
                    if (obj != null) {
                        clazz = obj.getClass();
                    }
                }
            } else {
                Object obj = tmp.iterator().next();
                if (obj != null) {
                    clazz = obj.getClass();
                }
            }
            sb.append(clazz.getSimpleName());
            sb.append(')');
        }
        return sb.toString();
    }

    /**
     * Mapping method for the stream.
     *
     * @param parameter to map.
     * @return the corresponding string result for the log.
     */
    public static String stream(Object parameter) {
        return parameter.getClass().getSimpleName();
    }
}
