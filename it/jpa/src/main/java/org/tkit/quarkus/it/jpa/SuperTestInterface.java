package org.tkit.quarkus.it.jpa;

import org.tkit.quarkus.log.rs.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public interface SuperTestInterface {

    @RestService(log = false)
    @GET
    @Path("ping21")
    default Response ping21() {
        return Response.ok().build();
    }

}
