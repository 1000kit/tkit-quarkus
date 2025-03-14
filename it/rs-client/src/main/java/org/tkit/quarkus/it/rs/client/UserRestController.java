package org.tkit.quarkus.it.rs.client;

import java.util.Arrays;

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
    @Path("ping")
    public Response ping() {
        return Response.ok().build();
    }

    @GET
    @Path("{id}")
    public Response getUser(@PathParam("id") String id) {
        try (Response response = client.getUserById(id)) {
            var tmp = response.readEntity(String.class);
            return Response.ok(tmp).build();
        }
    }

    @POST
    @Path("{id}")
    public Response createUser(@PathParam("id") String id, String data) {
        try (Response response = client.createUser(id, data)) {
            var tmp = response.readEntity(String.class);
            return Response.ok(tmp).build();
        }
    }

    @GET
    @Path("/")
    public Response getAllUser() {
        return client.getUserByIds(Arrays.asList(1L, 2L, 3L));
    }
}
