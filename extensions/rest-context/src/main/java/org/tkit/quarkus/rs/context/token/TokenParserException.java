package org.tkit.quarkus.rs.context.token;

public class TokenParserException extends RuntimeException {

    private final Enum<?> key;

    public TokenParserException(Enum<?> key, String message, Throwable throwable) {
        super(message, throwable);
        this.key = key;
    }

    public Enum<?> getKey() {
        return key;
    }
}