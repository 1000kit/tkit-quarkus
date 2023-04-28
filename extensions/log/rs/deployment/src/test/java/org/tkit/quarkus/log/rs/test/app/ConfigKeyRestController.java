package org.tkit.quarkus.log.rs.test.app;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.log.rs.LogRestService;

@LogRestService(configKey = "test")
@Path("configkey")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConfigKeyRestController {

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

    @LogRestService(configKey = "test", log = false)
    @GET
    @Path("test3")
    public Response test3() {
        return Response.ok("OK").build();
    }
}
