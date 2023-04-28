package org.tkit.quarkus.it.rs.client.reactive;

import java.util.concurrent.CompletionStage;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestPath;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserRestController {

    @Inject
    @RestClient
    UserRestsClient client;

    @GET
    @Path("{id}")
    public CompletionStage<Response> getUser(@RestPath("id") String id) {
        return client.getUserById(id);
    }

    @POST
    @Path("{id}")
    public CompletionStage<Response> createUser(@RestPath("id") String id, String data) {
        return client.createUser(id, data);
    }
}
