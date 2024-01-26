package org.tkit.quarkus.rs.context.runtime;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TenantAnnotationData {

    public Set<String> classes = new HashSet<>();

    public Map<String, Map<String, String>> methods = new HashMap<>();

    public void addExcludeClass(String name) {
        classes.add(name);
    }

    public boolean isExcludeClass(Class<?> clazz) {
        if (clazz == null) {
            return true;
        }
        return classes.contains(clazz.getName());
    }

    public void addExcludeMethod(String clazz, String method, String params) {
        methods.computeIfAbsent(clazz, k -> new HashMap<>())
                .put(method, params);
    }

    public boolean isExcludeMethod(Class<?> clazz, Method method) {
        if (clazz == null || method == null) {
            return true;
        }

        // check class methods
        var ms = methods.get(clazz.getName());
        if (ms == null) {
            return false;
        }

        // check methods
        var ps = ms.get(method.getName());
        if (ps == null) {
            return false;
        }

        // empty parameters and empty content
        if (ps.isEmpty() && method.getParameterTypes().length == 0) {
            return true;
        }

        // check parameter list
        StringBuilder params = new StringBuilder();
        for (Class<?> c : method.getParameterTypes()) {
            params.append(c.getName());
        }

        return ps.contentEquals(params);
    }
}
