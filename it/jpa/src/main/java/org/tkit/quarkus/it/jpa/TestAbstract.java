package org.tkit.quarkus.it.jpa;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
