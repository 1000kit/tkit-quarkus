package org.tkit.quarkus.log.rs.test.app;

import org.tkit.quarkus.log.rs.RestService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestService
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
