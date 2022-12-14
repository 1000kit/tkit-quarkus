package org.tkit.quarkus.it.jpa;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
