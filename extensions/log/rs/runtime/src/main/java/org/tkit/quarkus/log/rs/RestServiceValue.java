package org.tkit.quarkus.log.rs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RestServiceValue {

    static final Item DEFAULT = createItem(null);

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

    ClassItem getOrCreate(String clazz) {
        return classes.computeIfAbsent(clazz, c -> createClass(clazz));
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
        public Item config;
        public Map<String, Item> methods = new HashMap<>();

        public Map<String, Set<String>> mapping = new HashMap<>();

        Item getOrCreate(String method) {
            return methods.computeIfAbsent(method, c -> createItem(method));
        }

        boolean exists(String method) {
            return methods.containsKey(method);
        }

        void updateMapping() {
            methods.forEach((k,v) -> {
                if (v.configKey != null) {
                    mapping.computeIfAbsent(v.configKey, t -> new HashSet<>()).add(k);
                }
            });
        }

        List<Item> getByConfig(String key) {
            Set<String> mapMethods = mapping.get(key);

            // no config key
            if (mapMethods == null) {
                return List.of(getOrCreate(key));
            }

            // find config key
            return mapMethods.stream().map(x -> methods.get(x)).collect(Collectors.toList());
        }
    }

    public static class Item {

        public String id;

        public boolean log;

        public String configKey;
    }

    static ClassItem createClass(String clazz) {
        ClassItem c = new ClassItem();
        c.config = createItem(clazz);
        return c;
    }

    static Item createItem(String id) {
        Item item = new Item();
        item.id = id;
        item.log = true;
        item.configKey = null;
        return item;
    }
}
