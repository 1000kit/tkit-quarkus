package org.tkit.quarkus.dataimport.tests;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("test")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ParameterTestRestController {

    @Inject
    ParameterTestEntityDAO dao;

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") String id) {
        return Response.ok(dao.findById(id)).build();
    }
}
