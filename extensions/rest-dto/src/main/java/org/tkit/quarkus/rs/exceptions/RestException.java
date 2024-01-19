package org.tkit.quarkus.rs.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.Response;

/**
 * The REST exception. The DTO for this class {@link org.tkit.quarkus.rs.models.RestExceptionDTO}
 * THe exception mapper {@link org.tkit.quarkus.rs.mappers.DefaultExceptionMapper}
 */
@Deprecated
public class RestException extends RuntimeException {

    /**
     * The response status.
     */
    private Response.Status status;

    /**
     * The error code.
     */
    private Enum<?> errorCode;

    /**
     * The list of parameters.
     */
    private List<Object> parameters = new ArrayList<>();

    /**
     * The map of named parameters.
     */
    private Map<String, Object> namedParameters = new HashMap<>();

    /**
     * The default constructor.
     *
     * @param errorCode the error code.
     */
    public RestException(Enum<?> errorCode) {
        this(errorCode, Response.Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * The default constructor.
     *
     * @param errorCode the error code.
     * @param status the response status.
     */
    public RestException(Enum<?> errorCode, Response.Status status) {
        super(requireNonNull(errorCode));
        this.errorCode = errorCode;
        this.status = status;
    }

    /**
     * The default constructor.
     *
     * @param errorCode the error code.
     * @param status the response status.
     * @param cause the cause.
     */
    public RestException(Enum<?> errorCode, Response.Status status, Throwable cause) {
        super(requireNonNull(errorCode), cause);
        this.errorCode = errorCode;
        this.status = status;
    }

    /**
     * The default constructor.
     *
     * @param errorCode the error code.
     * @param cause the cause.
     */
    public RestException(Enum<?> errorCode, Throwable cause) {
        this(errorCode, Response.Status.INTERNAL_SERVER_ERROR, cause);
    }

    /**
     * The default constructor.
     *
     * @param errorCode the error code.
     * @param status the response status.
     * @param cause the cause.
     * @param params the list of parameters
     */
    public RestException(Enum<?> errorCode, Response.Status status, Throwable cause, Object... params) {
        this(errorCode, status, cause);
        if (params != null) {
            Collections.addAll(this.parameters, params);
        }
    }

    /**
     * The default constructor.
     *
     * @param errorCode the error code.
     * @param cause the cause.
     * @param params the list of parameters
     */
    public RestException(Enum<?> errorCode, Throwable cause, Object... params) {
        this(errorCode, Response.Status.INTERNAL_SERVER_ERROR, cause, params);
    }

    /**
     * The default constructor.
     *
     * @param errorCode the error code.
     * @param status the response status.
     * @param params the list of parameters
     */
    public RestException(Enum<?> errorCode, Response.Status status, Object... params) {
        this(errorCode, status);
        if (params != null) {
            Collections.addAll(this.parameters, params);
        }
    }

    /**
     * The default constructor.
     *
     * @param errorCode the error code.
     * @param params the list of parameters
     */
    public RestException(Enum<?> errorCode, Object... params) {
        this(errorCode, Response.Status.INTERNAL_SERVER_ERROR, params);
    }

    /**
     * Adds the parameter with name.
     *
     * @param name the name of the parameter.
     * @param value the value of the parameter.
     * @return this instance.
     */
    public RestException addParam(String name, Object value) {
        this.namedParameters.put(name, value);
        return this;
    }

    /**
     * Add the parameter to the list of parameters.
     *
     * @param value the parameter value.
     * @return this instance.
     */
    public RestException addParam(Object value) {
        this.parameters.add(value);
        return this;
    }

    /**
     * Add the list of parameters to the exception.
     *
     * @param params the list of parameters.
     * @return this instance.
     */
    public RestException withParams(Object... params) {
        Collections.addAll(this.parameters, params);
        return this;
    }

    /**
     * Add the map of named parameters to the exception.
     *
     * @param namedParams the named parameters.
     * @return this instance.
     */
    public RestException withNamedParams(Map<String, Object> namedParams) {
        this.namedParameters.putAll(namedParams);
        return this;
    }

    /**
     * Check if enum is not null.
     *
     * @param errorCode the error enumeration.
     * @return the name of the enumeration.
     * @throws NullPointerException if the {@code errorCode} is null.
     */
    private static String requireNonNull(Enum<?> errorCode) {
        if (errorCode == null) {
            throw new NullPointerException("Error code is null!");
        }
        return errorCode.name();
    }

    public Response.Status getStatus() {
        return status;
    }

    public Enum<?> getErrorCode() {
        return errorCode;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public Map<String, Object> getNamedParameters() {
        return namedParameters;
    }
}
