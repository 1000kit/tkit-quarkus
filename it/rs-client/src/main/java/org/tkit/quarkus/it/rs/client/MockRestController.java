package org.tkit.quarkus.it.rs.client;

import java.util.UUID;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.security.Authenticated;

@Authenticated
@Path("/mock/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MockRestController {

    @GET
    @Path("{id}")
    public Response getUser(@PathParam("id") String id) {
        return Response.ok(new Data()).build();
    }

    @POST
    @Path("{id}")
    public Response createUser(@PathParam("id") String id, String data) {
        return Response.ok(new Data()).build();
    }

    @GET
    @Path("/")
    public Response getAllUser() {
        return Response.ok(new Data()).build();
    }

    public static class Data {
        public String id = UUID.randomUUID().toString();
    }
}
