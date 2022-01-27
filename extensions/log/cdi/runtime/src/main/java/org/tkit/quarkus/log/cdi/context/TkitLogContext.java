package org.tkit.quarkus.log.cdi.context;

import org.slf4j.MDC;

/**
 * Log context holder, mostly useful for propagation of values through the invocation chaing.
 * Use ThreadLocal as storage. Will be propagateg to other threads via MP Context Propagation if it is enabled see {@link TkitLogThreadContextProvider}
 */
public class TkitLogContext {
    public static final String X_CORRELATION_ID = "X-Correlation-ID";
    private static ThreadLocal<CorrelationScope> userContext = new ThreadLocal<>();

    /**
     * Get current value of tkit log context. Can be null.
     * @return current context or null if empty.
     */
    public static CorrelationScope get() {
        return userContext.get();
    }

    /**
     * Set the context value.
     * @param ctx the new context to set.
     */
    public static void set(CorrelationScope ctx) {
        userContext.set(ctx);
        if (ctx != null) {
            MDC.put(X_CORRELATION_ID, ctx.correlationId);
        } else {
            MDC.remove(X_CORRELATION_ID);
        }
    }
}
