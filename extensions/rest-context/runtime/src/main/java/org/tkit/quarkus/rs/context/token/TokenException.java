package org.tkit.quarkus.rs.context.token;

public class TokenException extends RuntimeException {

    private final Enum<?> key;

    public TokenException(Enum<?> key, String message) {
        super(message);
        this.key = key;
    }

    public TokenException(Enum<?> key, String message, Throwable throwable) {
        super(message, throwable);
        this.key = key;
    }

    public Enum<?> getKey() {
        return key;
    }
}