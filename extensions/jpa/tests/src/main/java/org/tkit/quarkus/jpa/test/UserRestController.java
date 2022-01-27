package org.tkit.quarkus.jpa.test;

import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
