package org.tkit.quarkus.log.rs.test.app;

import org.tkit.quarkus.log.rs.RestService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestService
@Path("properties")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PropertiesRestController {

    @GET
    @Path("test1")
    public Response test1() {
        return Response.ok("OK").build();
    }

    @RestService(log = false)
    @GET
    @Path("test2")
    public Response test2() {
        return Response.ok("OK").build();
    }

}
