package org.tkit.quarkus.it.rs.client;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserRestController {

    @Inject
    @RestClient
    UserRestsClient client;

    @GET
    @Path("{id}")
    public Response getUser(@PathParam("id") String id) {
        return Response.fromResponse(client.getUserById(id)).build();
    }

    @POST
    @Path("{id}")
    public Response createUser(@PathParam("id") String id, String data) {
        return Response.fromResponse(client.createUser(id, data)).build();
    }
}
