package org.tkit.quarkus.dataimport.tests;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
