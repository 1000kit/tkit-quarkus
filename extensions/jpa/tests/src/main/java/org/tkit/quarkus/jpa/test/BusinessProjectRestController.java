package org.tkit.quarkus.jpa.test;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.jpa.daos.Page;

@Path("business/projects")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BusinessProjectRestController {

    @Inject
    BusinessProjectDAO dao;

    @GET
    @Path("{id}")
    public Response find(@PathParam("id") String id) {
        BusinessProject tmp = dao.findById(id);
        if (tmp == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(tmp).build();
    }

    @GET
    @Path("page/{index}/{size}")
    public Response page(@PathParam("index") int index, @PathParam("size") int size) {
        return Response.ok(dao.createPageQuery(Page.of(index, size)).getPageResult()).build();
    }

    @POST
    public Response create(BusinessProject project) {
        return Response.ok(dao.create(project)).build();
    }
}
