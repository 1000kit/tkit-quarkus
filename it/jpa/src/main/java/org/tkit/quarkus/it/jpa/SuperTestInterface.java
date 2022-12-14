package org.tkit.quarkus.it.jpa;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.tkit.quarkus.log.rs.LogRestService;

public interface SuperTestInterface {

    @LogRestService(log = false)
    @GET
    @Path("ping21")
    default Response ping21() {
        return Response.ok().build();
    }

}
