package org.tkit.quarkus.it.rs.client.reactive;

import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
