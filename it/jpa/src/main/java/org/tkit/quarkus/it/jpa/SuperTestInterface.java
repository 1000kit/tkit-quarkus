package org.tkit.quarkus.it.jpa;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.log.rs.LogRestService;

public interface SuperTestInterface {

    @LogRestService(log = false)
    @GET
    @Path("ping21")
    default Response ping21() {
        return Response.ok().build();
    }

}
