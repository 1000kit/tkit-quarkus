package org.tkit.quarkus.log.cdi.interceptor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.tkit.quarkus.log.cdi.LogService;

public class LogServiceContainer {

    Map<String, LogServiceClass> classes = new ConcurrentHashMap<>();

    public static void getLogService(Class<?> clazz, Method method) {
        LogService ma = method.getAnnotation(LogService.class);
        if (ma == null) {
            LogService ca = clazz.getAnnotation(LogService.class);
            if (ca == null) {
                return;
            }
        }
    }

    public static class LogServiceClass {

        Map<String, LogServiceMethod> methods = new ConcurrentHashMap<>();

    }

    public static class LogServiceMethod {

    }
}
