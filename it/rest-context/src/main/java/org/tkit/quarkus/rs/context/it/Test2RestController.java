package org.tkit.quarkus.rs.context.it;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.log.cdi.LogService;
import org.tkit.quarkus.rs.context.tenant.TenantExclude;

@Path("test2")
@LogService
@TenantExclude
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Test2RestController {

    @GET
    @Path("1")
    public Response test1() {
        return Response.ok().build();
    }

    @GET
    @Path("2")
    public Response test2() {
        return Response.ok().build();
    }
}
