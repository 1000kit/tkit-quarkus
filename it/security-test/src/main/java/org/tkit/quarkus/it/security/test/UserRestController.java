package org.tkit.quarkus.it.security.test;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestPath;

import io.quarkus.security.PermissionsAllowed;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserRestController {

    @GET
    @Path("{id}")
    @PermissionsAllowed("ocx-user:read")
    public Response getUser(@RestPath("id") String id) {
        return Response.ok().build();
    }

    @POST
    @Path("{id}")
    @PermissionsAllowed("ocx-user:write")
    public Response createUser(@RestPath("id") String id, String data) {
        return Response.ok().build();
    }

    @GET
    @Path("none/{id}")
    public Response getNone(@RestPath("id") String id) {
        return Response.ok().build();
    }
}
