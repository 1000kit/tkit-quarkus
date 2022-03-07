package org.tkit.quarkus.log.rs.test.app;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
