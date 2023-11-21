package org.tkit.quarkus.log.rs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

public class RestInterceptorContext {

    RestServiceValue.MethodItem ano;

    String method;

    String uri;

    String path;

    String durationString;

    long durationMillis;

    double durationSec;

    boolean exclude = false;

    Set<String> mdcKeys = new HashSet<>();

    private final long startTime;

    RestInterceptorContext() {
        this.startTime = System.currentTimeMillis();
    }

    public void close() {
        durationMillis = (System.currentTimeMillis() - startTime);
        durationSec = BigDecimal.valueOf(durationMillis / 1000f).setScale(3, RoundingMode.HALF_DOWN).doubleValue();
        durationString = String.format("%.3f", durationSec);
    }
}
