package org.tkit.quarkus.context;

import java.util.*;

public class Context {

    /**
     * User principal
     */
    private final String principal;

    /**
     * User tenant ID
     */
    private final String tenantId;

    /**
     * Correlation id that should be preserved throughout invocation chain
     */
    private final String correlationId;

    /**
     * Business context that should be preserved throughout invocation chain
     */
    private final String businessContext;

    /**
     * Additional metadata that should be preserved throughout invocation chain
     */
    private final Map<String, String> meta;

    private final Set<String> businessParams;

    /**
     * Stack of errors, which happens in the execution context.
     */
    private final Stack<ApplicationError> errors;

    Context(String correlationId, String businessContext, String principal, Map<String, String> meta, String tenantId) {
        this.correlationId = correlationId;
        this.businessContext = businessContext;
        this.meta = meta;
        this.principal = principal;
        this.businessParams = new HashSet<>();
        this.errors = new Stack<>();
        this.tenantId = tenantId;
    }

    /**
     * Gets the current principal.
     *
     * @return the current principal.
     */
    public String getPrincipal() {
        return principal;
    }

    /**
     * Gets business context.
     *
     * @return the business context.
     */
    public String getBusinessContext() {
        return businessContext;
    }

    /**
     * Gets user tenant ID.
     *
     * @return the user tenant ID.
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Gets the correlation ID.
     *
     * @return the correlation ID.
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Gets the context meta-data
     *
     * @return the context meta-data.
     */
    public Map<String, String> getMeta() {
        return meta;
    }

    public void addMeta(String key, String value) {
        if (key == null) {
            return;
        }
        meta.put(key, value);
    }

    public Set<String> getBusinessParams() {
        return businessParams;
    }

    public void addBusinessParams(String businessParam) {
        if (businessParam == null) {
            return;
        }
        businessParams.add(businessParam);
    }

    public void removeBusinessParams(String businessParam) {
        if (businessParam == null) {
            return;
        }
        businessParams.remove(businessParam);
    }

    public void clearBusinessParams() {
        businessParams.clear();
    }

    @Override
    public String toString() {
        return "correlationId=" + correlationId;
    }

    public ApplicationError getError() {
        return errors.peek();
    }

    public ApplicationError addError(Throwable throwable) {
        if (errors.isEmpty() || !errors.peek().throwable.equals(throwable)) {
            errors.push(new ApplicationError(throwable));
        }
        return errors.peek();
    }

    public static ApplicationContextBuilder builder() {
        return new ApplicationContextBuilder();
    }

    /**
     * Application error in the context
     */
    public static class ApplicationError {

        public Throwable throwable;

        /**
         * Show stack-strace
         */
        public boolean stacktrace;

        ApplicationError(Throwable throwable) {
            this.throwable = throwable;
        }

    }

    public static class ApplicationContextBuilder {

        private String principal;

        private String correlationId = UUID.randomUUID().toString();

        private String businessContext;

        private String tenantId;

        private final Map<String, String> meta = new HashMap<>();

        public ApplicationContextBuilder correlationId(String correlationId) {
            if (correlationId == null) {
                return this;
            }
            this.correlationId = correlationId;
            return this;
        }

        public ApplicationContextBuilder principal(String principal) {
            this.principal = principal;
            return this;
        }

        public ApplicationContextBuilder businessContext(String businessContext) {
            this.businessContext = businessContext;
            return this;
        }

        public ApplicationContextBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public ApplicationContextBuilder addMeta(String key, String value) {
            this.meta.put(key, value);
            return this;
        }

        public Context build() {
            return new Context(correlationId, businessContext, principal, meta, tenantId);
        }
    }
}