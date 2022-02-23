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
    public final String parameters;

    /**
     * The result value.
     */
    public String result;

    /**
     * The execution time.
     */
    public String time;

    /**
     * The default constructor.
     * @param method the method.
     * @param parameters the method parameters.
     */
    public InterceptorContext(String method, String parameters) {
        this.startTime = System.currentTimeMillis();
        this.method = method;
        this.parameters = parameters;
    }

    /**
     * Close the context.
     * @param result the result.
     */
    public void closeContext(String result) {
        time = String.format("%.3f", (System.currentTimeMillis() - startTime) / 1000f);
        this.result = result;
    }

}
