package org.tkit.quarkus.rs.context.token;

public class TokenException extends RuntimeException {

    private final Enum<?> key;

    public TokenException(Enum<?> key, String message, Throwable throwable) {
        super(message, throwable);
        this.key = key;
    }

    @SuppressWarnings("java:S1452")
    public Enum<?> getKey() {
        return key;
    }
}