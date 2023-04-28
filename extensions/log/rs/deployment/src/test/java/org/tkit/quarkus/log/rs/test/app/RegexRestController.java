package org.tkit.quarkus.log.rs.test.app;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.log.rs.LogRestService;

@LogRestService
@Path("regex")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegexRestController {

    @GET
    @Path("load")
    public Response load() {
        return Response.ok("OK").build();
    }

    @POST
    @Path("create")
    public Response create(String data) {
        return Response.ok(data).build();
    }
}
