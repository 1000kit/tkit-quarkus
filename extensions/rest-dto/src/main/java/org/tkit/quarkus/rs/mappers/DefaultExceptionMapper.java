package org.tkit.quarkus.rs.mappers;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.rs.exceptions.RestException;
import org.tkit.quarkus.rs.models.RestExceptionDTO;
import org.tkit.quarkus.rs.resources.ResourceManager;

/**
 * The default exception mapper with priority {@code PRIORITY}.
 */
@Deprecated
@Provider
@Priority(DefaultExceptionMapper.PRIORITY)
public class DefaultExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger log = LoggerFactory.getLogger(DefaultExceptionMapper.class);

    /**
     * The exception mapper priority
     */
    public static final int PRIORITY = 10000;

    /**
     * The request headers.
     */
    @Context
    private HttpHeaders headers;

    /**
     * The request URI info.
     */
    @Context
    UriInfo uriInfo;

    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(Exception e) {
        Optional<Boolean> logException = ConfigProvider.getConfig().getOptionalValue("tkit.rs.mapper.log", Boolean.class);
        if (logException.isEmpty() || logException.get()) {
            log.error("REST exception URL:{},ERROR:{}", uriInfo.getRequestUri(), e.getMessage());
        }
        if (e instanceof RestException) {
            return createResponse((RestException) e);
        }
        if (e instanceof WebApplicationException) {
            return createResponse((WebApplicationException) e);
        }
        return createResponse(e);
    }

    /**
     * Creates the response from the {@link RestException}
     *
     * @param e the {@link RestException}
     * @return the corresponding response.
     */
    private Response createResponse(RestException e) {
        RestExceptionDTO dto = new RestExceptionDTO();
        String message = ResourceManager.getMessage(e.getErrorCode(), getLocale(), e.getParameters());
        dto.setMessage(message);

        dto.setErrorCode(e.getErrorCode().name());
        dto.setParameters(e.getParameters());
        dto.setNamedParameters(e.getNamedParameters());
        return Response.status(e.getStatus()).type(mediaType()).entity(dto).build();
    }

    /**
     * Creates the response from the {@link WebApplicationException}
     *
     * @param e the {@link WebApplicationException}
     * @return the corresponding response.
     */
    protected Response createResponse(WebApplicationException e) {
        RestExceptionDTO dto = new RestExceptionDTO();
        dto.setErrorCode(Error.WEB_APPLICATION_EXCEPTION.name());
        dto.setMessage(e.getMessage());
        return Response.fromResponse(e.getResponse()).entity(dto).type(mediaType()).build();
    }

    /**
     * Creates the response from the {@link Exception}
     *
     * @param e the {@link Exception}
     * @return the corresponding response.
     */
    protected Response createResponse(Exception e) {
        RestExceptionDTO dto = new RestExceptionDTO();
        dto.setErrorCode(Error.UNDEFINED_ERROR_CODE.name());
        dto.setMessage(e.getMessage());
        return Response.serverError().type(mediaType()).entity(dto).build();
    }

    /**
     * Gets the media type for the response.
     *
     * @return the media type for the response.
     */
    protected MediaType mediaType() {
        return MediaType.APPLICATION_JSON_TYPE;
    }

    /**
     * Gets the response locale.
     *
     * @return the response locale.
     */
    protected Locale getLocale() {
        List<Locale> locales = headers.getAcceptableLanguages();
        if (locales != null && !locales.isEmpty()) {
            Locale tmp = locales.get(0);
            if (tmp != null && !"*".equals(tmp.getLanguage())) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * The exception mapper error codes.
     */
    public enum Error {

        /**
         * The error code for the web application exception {@link WebApplicationException}
         */
        WEB_APPLICATION_EXCEPTION,

        /**
         * The error code for undefined exception.
         */
        UNDEFINED_ERROR_CODE;
    }

}
