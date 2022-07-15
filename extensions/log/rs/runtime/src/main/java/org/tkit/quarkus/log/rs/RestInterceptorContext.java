package org.tkit.quarkus.log.rs;

import java.util.HashSet;
import java.util.Set;

public class RestInterceptorContext {

    RestServiceValue.MethodItem ano;

    String method;

    String uri;

    String path;

    String time;

    Long duration;

    boolean exclude = false;

    Set<String> mdcKeys = new HashSet<>();

    private final long startTime;

    RestInterceptorContext() {
        this.startTime = System.currentTimeMillis();
    }

    public void close() {
        duration = (System.currentTimeMillis() - startTime);
        time = String.format("%.3f", duration / 1000f);
    }
}
