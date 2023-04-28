package org.tkit.quarkus.log.rs.test.app;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("no-anno")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NoAnnotationRestController {

    @GET
    @Path("test1")
    public Response test1() {
        return Response.ok("OK").build();
    }

}
