package org.tkit.quarkus.rs.context;

public class RestContextException extends RuntimeException {

    private Enum<?> key;

    public RestContextException(Enum<?> key, String message) {
        super(message);
        this.key = key;
    }

    public RestContextException(Enum<?> key, String message, Throwable throwable) {
        super(message, throwable);
        this.key = key;
    }

    public Enum<?> getKey() {
        return key;
    }
}
