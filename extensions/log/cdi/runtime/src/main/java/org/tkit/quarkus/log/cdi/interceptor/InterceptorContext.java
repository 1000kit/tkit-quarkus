package org.tkit.quarkus.log.cdi.interceptor;

/**
 * The interceptor context.
 */
public class InterceptorContext {

    /**
     * The start time.
     */
    private final long startTime;

    /**
     * The service method.
     */
    public final String method;

    /**
     * The list of method parameters.
     */
    public String parameters;

    /**
     * The execution time.
     */
    public String time;

    /**
     * The default constructor.
     * @param method the method.
     */
    public InterceptorContext(String method) {
        this.startTime = System.currentTimeMillis();
        this.method = method;
    }

    /**
     * Close the context.
     */
    public void close() {
        time = String.format("%.3f", (System.currentTimeMillis() - startTime) / 1000f);
    }

}
