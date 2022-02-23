package org.tkit.quarkus.it.jpa;

import org.tkit.quarkus.log.rs.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public interface TestInterface extends SuperTestInterface {

    @RestService(log = false)
    @GET
    @Path("ping")
    default Response ping() {
        return Response.ok().build();
    }

    Response ping2();
}
