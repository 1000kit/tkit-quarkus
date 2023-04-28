package org.tkit.quarkus.log.rs.test.app;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.log.rs.LogRestService;

@LogRestService
@Path("anno")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AnnotationRestController {

    @GET
    @Path("test1")
    public Response test1() {
        return Response.ok("OK").build();
    }

    @LogRestService(log = false)
    @GET
    @Path("test2")
    public Response test2() {
        return Response.ok("OK").build();
    }
}
