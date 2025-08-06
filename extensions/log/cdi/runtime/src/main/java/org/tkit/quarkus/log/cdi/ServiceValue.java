package org.tkit.quarkus.log.cdi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ServiceValue {

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

    public ClassItem getOrCreate(String clazz) {
        return classes.computeIfAbsent(clazz, c -> createClass(clazz));
    }

    public void updateMapping() {
        classes.forEach((k, v) -> {
            if (v.config != null && v.config.configKey != null) {
                mapping.computeIfAbsent(v.config.configKey, t -> new HashSet<>()).add(k);
            }
            v.updateMapping();
        });
    }

    void updateConfig() {
        classes.forEach((k, v) -> v.methods.forEach((mk, mv) -> {
            if (mv.config == null) {
                mv.config = v.config;
            }
        }));
    }

    public static class ClassItem {
        public LogServiceAnnotation config;
        public String id;
        public Map<String, MethodItem> methods = new HashMap<>();

        public Map<String, Set<String>> mapping = new HashMap<>();

        public MethodItem getOrCreate(String method) {
            return methods.computeIfAbsent(method, c -> createMethod(method));
        }

        void updateMapping() {
            methods.forEach((k, v) -> {
                if (v.config != null && v.config.configKey != null) {
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

        public LogService.Log logStart;
    }

    public static class MethodItem {
        public LogServiceAnnotation config;
        public String id;
        public String returnMask;
        public Map<Short, String> params;

        public void copyConfig(LogServiceAnnotation config) {
            this.config = createConfig();
            this.config.log = config.log;
            this.config.stacktrace = config.stacktrace;
            this.config.logStart = config.logStart;
        }
    }

    static MethodItem createMethod(String id) {
        MethodItem c = new MethodItem();
        c.id = id;
        c.config = null;
        return c;
    }

    static ClassItem createClass(String id) {
        ClassItem c = new ClassItem();
        c.id = id;
        c.config = null;
        return c;
    }

    public static LogServiceAnnotation createConfig() {
        LogServiceAnnotation item = new LogServiceAnnotation();
        item.log = false;
        item.stacktrace = false;
        item.configKey = null;
        item.logStart = LogService.Log.DEFAULT;
        return item;
    }

}
