package org.tkit.quarkus.context;

import org.slf4j.MDC;

public class ApplicationContextThread {

    public static final String MDC_X_CORRELATION_ID = "X-Correlation-ID";

    private static final ThreadLocal<ApplicationContext> CONTEXT = new ThreadLocal<>();

    /**
     * Get current value of application context. Can be null.
     * @return current context or null if empty.
     */
    public static ApplicationContext get() {
        return CONTEXT.get();
    }

    /**
     * Set the context value.
     * @param ctx the new context to set.
     */
    public static void set(ApplicationContext ctx) {
        CONTEXT.set(ctx);
        if (ctx != null) {
            MDC.put(MDC_X_CORRELATION_ID, ctx.correlationId);
        } else {
            MDC.remove(MDC_X_CORRELATION_ID);
        }
    }

    public static boolean isEmpty() {
        return get() == null;
    }

    public static void start(ApplicationContext ctx) {
        set(ctx);
    }

    public static void start() {
        set(ApplicationContext.builder().build());
    }

    public static void close() {
        set(null);
    }
}
