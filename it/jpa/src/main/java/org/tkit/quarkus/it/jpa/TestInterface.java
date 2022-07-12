package org.tkit.quarkus.it.jpa;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.tkit.quarkus.log.rs.RestService;

public interface TestInterface extends SuperTestInterface {

    @RestService(log = false)
    @GET
    @Path("ping")
    default Response ping() {
        return Response.ok().build();
    }

    Response ping2();
}
