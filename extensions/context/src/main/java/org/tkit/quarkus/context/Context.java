package org.tkit.quarkus.context;

import java.util.*;

public class Context {

    /**
     * User principal
     */
    public final String principal;

    /**
     * Correlation id that should be preserved throughout invocation chain
     */
    public final String correlationId;

    /**
     * Business context that should be preserved throughout invocation chain
     */
    public final String businessContext;

    /**
     * Additional metadata that should be preserved throughout invocation chain
     */
    public final Map<String, String> meta;

    final Set<String> businessParams;

    /**
     * Stack of errors, which happens in the execution context.
     */
    final Stack<ApplicationError> errors;

    Context(String correlationId, String businessContext, String principal, Map<String, String> meta) {
        this.correlationId = correlationId;
        this.businessContext = businessContext;
        this.meta = meta;
        this.principal = principal;
        this.businessParams = new HashSet<>();
        this.errors = new Stack<>();
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

    public static class ApplicationError {

        public Throwable throwable;

        public boolean stacktrace;

        ApplicationError(Throwable throwable) {
            this.throwable = throwable;
        }

    }

    public static class ApplicationContextBuilder {

        private String principal;

        private String correlationId = UUID.randomUUID().toString();

        private String businessContext;

        private Map<String, String> meta = new HashMap<>();

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

        public Context build() {
            return new Context(correlationId, businessContext, principal, meta);
        }
    }
}