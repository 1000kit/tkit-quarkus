package org.tkit.quarkus.rs;

import java.util.List;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigMapping(prefix = "tkit.rs")
public interface RestConfig {

    /**
     * List of HTTP headers to propagate from request to response.
     *
     * <p>
     * When specified, the {@code HeaderResponsePropagationFilter} will copy these headers
     * from the incoming request to the outgoing response. Only headers that are present
     * in the request and match the names in this list will be propagated.
     * </p>
     *
     * <p>
     * <b>Example configuration:</b>
     * </p>
     *
     * <pre>
     * tkit.rs.response.propagation.headers=X-Business-Context,X-Correlation-ID
     * </pre>
     *
     * <p>
     * By default, no headers are propagated.
     * </p>
     */
    @WithName("response.propagation.headers")
    @WithDefault("[]")
    List<String> responsePropagationHeaders();

}
