package org.tkit.quarkus.jpa.test;

import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserRestController {

    @Inject
    UserDAO userDAO;

    @GET
    @Path("search")
    public Response page(@BeanParam UserSearchCriteriaDTO dto) {

        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setName(dto.name);
        criteria.setEmail(dto.email);
        return Response.ok(userDAO.pageUsers(criteria, Page.of(dto.index, dto.size)).getPageResult()).build();
    }

    @GET
    @Path("page/{index}/{size}")
    public Response page(@PathParam("index") int index, @PathParam("size") int size) {
        return Response.ok(userDAO.createPageQuery(Page.of(index, size)).getPageResult()).build();
    }

    @GET
    @Path("pageHeader/{index}/{size}")
    public Response pageHeader(@PathParam("index") int index, @PathParam("size") int size) {
        PageResult<User> result = userDAO.createPageQuery(Page.of(index, size)).getPageResult();

        return Response
                .ok(result.getStream())
                .header("totalElements", result.getTotalElements())
                .header("number", result.getNumber())
                .header("size", result.getSize())
                .header("totalPages", result.getTotalPages())
                .build();
    }

    @GET
    @Path("{id}")
    public Response find(@PathParam("id") String id) {
        User tmp = userDAO.findById(id);
        if (tmp == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(tmp).build();
    }

    @POST
    public Response create(User user) {
        return Response.ok(userDAO.create(user)).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") String id, User user) {
        User tmp = userDAO.findById(id);
        if (tmp == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        tmp.setEmail(user.getEmail());
        tmp.setName(user.getName());
        return Response.ok(userDAO.update(tmp)).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") String id) {
        return Response.ok(userDAO.deleteQueryById(id)).build();
    }
}
