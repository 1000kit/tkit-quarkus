package org.tkit.quarkus.log.cdi.interceptor;

import org.tkit.quarkus.log.cdi.LogService;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
