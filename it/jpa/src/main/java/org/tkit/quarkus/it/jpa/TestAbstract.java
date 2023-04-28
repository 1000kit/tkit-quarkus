package org.tkit.quarkus.it.jpa;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.log.rs.LogRestService;

public class TestAbstract extends SuperTestAbstract {

    @LogRestService(log = false)
    @GET
    @Path("test")
    @Override
    public Response test() {
        return Response.ok().build();
    }
}
