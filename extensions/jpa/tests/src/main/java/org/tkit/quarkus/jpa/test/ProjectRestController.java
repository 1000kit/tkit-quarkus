package org.tkit.quarkus.jpa.test;

import org.tkit.quarkus.jpa.daos.Page;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("projects")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProjectRestController {

    @Inject
    ProjectDAO projectDAO;

    @GET
    @Path("{id}")
    public Response find(@PathParam("id") String id) {
        Project tmp = projectDAO.findById(id);
        if (tmp == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(tmp).build();
    }

    @GET
    @Path("page/{index}/{size}")
    public Response page(@PathParam("index") int index, @PathParam("size") int size) {
        return Response.ok(projectDAO.createPageQuery(Page.of(index, size)).getPageResult()).build();
    }

    @POST
    public Response create(Project project) {
        return Response.ok(projectDAO.create(project)).build();
    }
}
