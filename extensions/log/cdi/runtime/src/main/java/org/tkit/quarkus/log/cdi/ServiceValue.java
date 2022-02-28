package org.tkit.quarkus.log.cdi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ServiceValue {

    static final LogServiceAnnotation DEFAULT_ANO = createLogServiceAnnotation();

    public Map<String, ClassItem> classes = new HashMap<>();

    public Map<String, Set<String>> mapping = new HashMap<>();

    List<ClassItem> getByConfig(String key) {
        Set<String> mapClasses = mapping.get(key);

        // no config key
        if (mapClasses == null) {
            ClassItem item = classes.get(key);
            // no config key no class name
            if (item == null) {
                return null;
            }
            // find base on class name
            return List.of(item);
        }

        // find config key
        return mapClasses.stream().map(x -> classes.get(x)).collect(Collectors.toList());
    }

    ClassItem get(String clazz) {
        return classes.get(clazz);
    }

    ClassItem getOrCreate(String clazz) {
        return classes.computeIfAbsent(clazz, c -> createClass(null));
    }

    ClassItem getOrCreate(String clazz, LogServiceAnnotation a) {
        return classes.computeIfAbsent(clazz, c -> createClass(a));
    }

    boolean exists(String clazz) {
        return classes.containsKey(clazz);
    }

    void updateMapping() {
        classes.forEach((k,v) -> {
            if (v.config.configKey != null) {
                mapping.computeIfAbsent(v.config.configKey, t -> new HashSet<>()).add(k);
            }
            v.updateMapping();
        });
    }

    public static class ClassItem {
        public LogServiceAnnotation config;
        public Map<String, MethodItem> methods = new HashMap<>();

        public Map<String, Set<String>> mapping = new HashMap<>();

        MethodItem getOrCreate(String method) {
            return methods.computeIfAbsent(method, c -> createMethod(null));
        }

        MethodItem getOrCreate(String method, ClassItem parent) {
            return methods.computeIfAbsent(method, c -> createMethod(parent.config));
        }

        boolean exists(String method) {
            return methods.containsKey(method);
        }

        void updateMapping() {
            methods.forEach((k,v) -> {
                if (v.config.configKey != null) {
                    mapping.computeIfAbsent(v.config.configKey, t -> new HashSet<>()).add(k);
                }
            });
        }

        List<MethodItem> getByConfig(String key) {
            Set<String> mapMethods = mapping.get(key);

            // no config key
            if (mapMethods == null) {
                return List.of(getOrCreate(key));
            }

            // find config key
            return mapMethods.stream().map(x -> methods.get(x)).collect(Collectors.toList());
        }
    }

    public static class LogServiceAnnotation {

        public boolean log;

        public boolean stacktrace;

        public String configKey;
    }

    public static class MethodItem {
        public LogServiceAnnotation config;
        public String returnMask;
        public Map<Short, String> params;
    }

    static MethodItem createMethod(LogServiceAnnotation a) {
        MethodItem c = new MethodItem();
        c.config = Objects.requireNonNullElseGet(a, ServiceValue::createLogServiceAnnotation);
        return c;
    }

    static ClassItem createClass(LogServiceAnnotation a) {
        ClassItem c = new ClassItem();
        c.config = Objects.requireNonNullElseGet(a, ServiceValue::createLogServiceAnnotation);
        return c;
    }

    static LogServiceAnnotation createLogServiceAnnotation() {
        LogServiceAnnotation item = new LogServiceAnnotation();
        item.log = false;
        item.stacktrace = false;
        item.configKey = null;
        return item;
    }

}
