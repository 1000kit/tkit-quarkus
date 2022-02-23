package org.tkit.quarkus.it.jpa;

import org.tkit.quarkus.log.rs.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class TestAbstract extends SuperTestAbstract {

    @RestService(log = false)
    @GET
    @Path("test")
    @Override
    public Response test() {
        return Response.ok().build();
    }
}
