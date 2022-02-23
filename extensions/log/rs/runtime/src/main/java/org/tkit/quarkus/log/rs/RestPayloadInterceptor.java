package org.tkit.quarkus.log.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Provider
public class RestPayloadInterceptor implements ContainerRequestFilter {

    /**
     * The resource info.
     */
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        RestRuntimeConfig config = RestRecorder.getConfig();
        if (config.payload.enabled) {

            if (requestContext.getMethod().equals(HttpMethod.POST) || requestContext.getMethod().equals(HttpMethod.PUT)) {
                Logger logger = LoggerFactory.getLogger(resourceInfo.getResourceClass());
                InputStream stream = requestContext.getEntityStream();
                if (!stream.markSupported()) {
                    stream = new BufferedInputStream(stream);
                }
                stream.mark(config.payload.maxEntitySize + 1);
                StringBuilder sb = new StringBuilder();
                stream.mark(config.payload.maxEntitySize + 1);
                final byte[] entity = new byte[config.payload.maxEntitySize + 1];
                final int entitySize = stream.read(entity);
                if (entitySize <= 0) {
                    if (config.payload.emptyBodyEnabled) {
                        sb.append(config.payload.emptyBodyMessage);
                    }
                } else {
                    sb.append(new String(entity, 0, Math.min(entitySize, config.payload.maxEntitySize), StandardCharsets.UTF_8));
                    if (entitySize > config.payload.maxEntitySize) {
                        sb.append(config.payload.pageMessage);
                    }
                }
                if (sb.length() > 0) {
                    logger.info(String.format(config.payload.message, requestContext.getMethod(), requestContext.getUriInfo().getRequestUri(), sb));
                }
                stream.reset();
                requestContext.setEntityStream(stream);
            }
        }

    }
}
