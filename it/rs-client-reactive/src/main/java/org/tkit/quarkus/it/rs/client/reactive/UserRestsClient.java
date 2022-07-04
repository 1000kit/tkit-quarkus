package org.tkit.quarkus.it.rs.client.reactive;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;

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