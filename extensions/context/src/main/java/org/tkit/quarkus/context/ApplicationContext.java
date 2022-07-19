package org.tkit.quarkus.context;

import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.MDC;

public class ApplicationContext {

    public static final String MDC_X_CORRELATION_ID = "X-Correlation-ID";

    private static final ThreadLocal<Context> CONTEXT = new ThreadLocal<>();

    /**
     * Get current value of application context. Can be null.
     *
     * @return current context or null if empty.
     */
    public static Context get() {
        return CONTEXT.get();
    }

    /**
     * Set the context value.
     *
     * @param ctx the new context to set.
     */
    static void set(Context ctx) {
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

    public static void start(Context ctx) {
        set(ctx);
    }

    public static void start() {
        set(Context.builder().build());
    }

    public static void close() {
        if (!isEmpty()) {
            removeAllBusinessLogParams();
            set(null);
        }
    }

    private static final String BUSINESS_DATA_PREFIX = ConfigProvider.getConfig()
            .getOptionalValue("tkit.log.customdata.prefix", String.class).orElse("business_information_");

    public static void addBusinessLogParam(String key, String value) {
        String k = BUSINESS_DATA_PREFIX + key;
        get().businessParams.add(k);
        MDC.put(k, value);
    }

    public static void removeBusinessLogParam(String key) {
        String k = BUSINESS_DATA_PREFIX + key;
        get().businessParams.remove(k);
        MDC.remove(k);
    }

    public static void removeAllBusinessLogParams() {
        get().businessParams.forEach(MDC::remove);
        get().businessParams.clear();
    }
}
