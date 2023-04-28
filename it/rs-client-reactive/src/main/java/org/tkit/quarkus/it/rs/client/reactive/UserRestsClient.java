package org.tkit.quarkus.it.rs.client.reactive;

import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/users")
@RegisterRestClient(configKey = "user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserRestsClient {

    @GET
    @Path("/{id}")
    CompletionStage<Response> getUserById(@PathParam("id") String id);

    @POST
    @Path("/{id}")
    CompletionStage<Response> createUser(@PathParam("id") String id, String data);
}
