package org.tkit.quarkus.log.cdi.interceptor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import org.tkit.quarkus.log.cdi.LogParam;

/**
 * Logger builder service.
 */
public class LogParamValueService {

    /**
     * Gets the map of the mapping function for the class.
     */
    public static Map<Class<?>, Function<Object, String>> CLASSES = new ConcurrentHashMap<>(JavaTypesLogParamValue.classes());

    /**
     * Gets the map of the mapping function for the assignable class.
     */
    public static Map<Class<?>, Function<Object, String>> ASSIGNABLE_FROM = new HashMap<>(
            JavaTypesLogParamValue.assignableFrom());

    public static void init(Stream<LogParam> services) {
        if (services != null) {
            Map<Class<?>, LogParam.Item> classes = new HashMap<>();
            Map<Class<?>, LogParam.Item> assignable = new HashMap<>();
            services.forEach(def -> {
                if (def.getClasses() != null) {
                    map(def.getClasses(), classes);
                }
                if (def.getAssignableFrom() != null) {
                    map(def.getAssignableFrom(), assignable);
                }
            });
            classes.forEach((c, item) -> CLASSES.put(c, item.fn));
            assignable.forEach((c, item) -> ASSIGNABLE_FROM.put(c, item.fn));
        }
    }

    private static void map(List<LogParam.Item> items, Map<Class<?>, LogParam.Item> target) {
        if (items != null) {
            for (LogParam.Item item : items) {
                LogParam.Item tmp = target.get(item.clazz);
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
    public static String getParameterValue(Object parameter) {
        if (parameter == null) {
            return null;
        }

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

        return "" + parameter;
    }

}
