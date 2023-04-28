package org.tkit.quarkus.it.jpa;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.log.rs.LogRestService;

public class SuperTestAbstract {

    @LogRestService(log = false)
    @GET
    @Path("test21")
    public Response test21() {
        return Response.ok().build();
    }

    @LogRestService(log = true)
    @GET
    @Path("test")
    public Response test() {
        return Response.ok().build();
    }
}
