package org.tkit.quarkus.it.jpa;

import org.tkit.quarkus.log.rs.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class SuperTestAbstract {

    @RestService(log = false)
    @GET
    @Path("test21")
    public Response test21() {
        return Response.ok().build();
    }

    @RestService(log = true)
    @GET
    @Path("test")
    public Response test() {
        return Response.ok().build();
    }
}
