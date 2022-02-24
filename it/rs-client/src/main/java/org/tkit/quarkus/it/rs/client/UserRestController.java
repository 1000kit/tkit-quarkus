package org.tkit.quarkus.it.rs.client;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
