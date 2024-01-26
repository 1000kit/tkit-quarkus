package org.tkit.quarkus.rs.context.it;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.log.cdi.LogService;
import org.tkit.quarkus.rs.context.tenant.TenantExclude;

@Path("test1")
@LogService
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Test1RestController {

    @GET
    @Path("1")
    public Response test1() {
        return Response.ok().build();
    }

    @GET
    @Path("2")
    @TenantExclude
    public Response test2() {
        return Response.ok().build();
    }

    @GET
    @Path("3/{id}/{p}")
    @TenantExclude
    public Response test3(@PathParam("id") String id, @PathParam("p") Integer p) {
        return Response.ok(id).build();
    }
}
