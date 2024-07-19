package org.tkit.quarkus.it.rs.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/users")
@RegisterRestClient(configKey = "user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserRestsClient {

    @GET
    @Path("/{id}")
    Response getUserById(@PathParam("id") String id);

    @POST
    @Path("/{id}")
    Response createUser(@PathParam("id") String id, String data);

    @GET
    @Path("/")
    Response getUserByIds(@QueryParam("ids") List<Object> ids);
}
