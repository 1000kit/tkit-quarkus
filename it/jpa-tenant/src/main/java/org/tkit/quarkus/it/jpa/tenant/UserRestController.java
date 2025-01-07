package org.tkit.quarkus.it.jpa.tenant;

import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserRestController {

    @Inject
    UserDAO dao;

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") String id) {
        User user = dao.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        UserDTO dto = new UserDTO();
        dto.modificationCount = user.getModificationCount();
        dto.id = user.getId();
        dto.username = user.username;
        dto.email = user.email;
        return Response.ok(dto).build();
    }

    @POST
    public Response create(UserDTO dto) {
        User user = new User();
        user.username = dto.username;
        user.email = dto.email;
        dao.create(user);

        return Response.ok(user).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") String id, UserDTO dto) {
        var user = dao.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        user.setModificationCount(dto.modificationCount);
        user.username = dto.username;
        user.email = dto.email;
        try {
            user = dao.update(user);
        } catch (OptimisticLockException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        UserDTO r = new UserDTO();
        r.modificationCount = user.getModificationCount();
        r.id = user.getId();
        r.username = user.username;
        r.email = user.email;

        return Response.ok(r).build();
    }

    @GET
    public Response all() {
        UserListDTO dto = new UserListDTO();
        dto.items = dao.findAll().map(user -> {
            UserDTO r = new UserDTO();
            r.modificationCount = user.getModificationCount();
            r.id = user.getId();
            r.username = user.username;
            r.email = user.email;
            return r;
        }).toList();
        return Response.ok(dto).build();
    }
}
