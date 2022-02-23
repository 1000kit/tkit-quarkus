package org.tkit.quarkus.log.rs;

import java.util.HashSet;
import java.util.Set;

public class RestInterceptorContext {

    RestServiceValue.Item ano;

    String method;

    String uri;

    String path;

    String time;

    boolean exclude = false;

    Set<String> mdcKeys = new HashSet<>();

    private final long startTime;

    RestInterceptorContext() {
        this.startTime = System.currentTimeMillis();
    }

    public void close() {
        time = String.format("%.3f", (System.currentTimeMillis() - startTime) / 1000f);
    }
}
