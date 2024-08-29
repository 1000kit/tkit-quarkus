package org.tkit.quarkus.rs.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * The rest exception DTO model.
 *
 * @deprecated will be removed in next major release.
 */
@Deprecated(since = "1.0.0")
@RegisterForReflection
@SuppressWarnings("java:S1133")
public class RestExceptionDTO {

    /**
     * The error code.
     */
    private String errorCode;

    /**
     * The message.
     */
    private String message;

    /**
     * The error parameters.
     */
    private List<Object> parameters;

    /**
     * The named parameters.
     */
    private Map<String, Object> namedParameters = new HashMap<>();

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Object> getNamedParameters() {
        return namedParameters;
    }

    public void setNamedParameters(Map<String, Object> namedParameters) {
        this.namedParameters = namedParameters;
    }
}
