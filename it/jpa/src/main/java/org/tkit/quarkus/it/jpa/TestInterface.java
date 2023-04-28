package org.tkit.quarkus.it.jpa;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.log.rs.LogRestService;

public interface TestInterface extends SuperTestInterface {

    @LogRestService(log = false)
    @GET
    @Path("ping")
    default Response ping() {
        return Response.ok().build();
    }

    Response ping2();
}
