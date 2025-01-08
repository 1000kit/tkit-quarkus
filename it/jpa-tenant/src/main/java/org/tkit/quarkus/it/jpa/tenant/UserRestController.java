package org.tkit.quarkus.it.jpa.tenant;

import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;

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
        return Response.ok(map(user)).build();
    }

    @POST
    public Response create(UserDTO dto) {
        User user = new User();
        user.username = dto.username;
        user.email = dto.email;
        dao.create(user);

        return Response.ok(user).build();
    }

    @POST
    @Path("custom/{tenantId}")
    public Response createCustom(@PathParam("tenantId") String tenantId, UserDTO dto) {

        try {
            var ctx = Context.builder()
                    .principal("test")
                    .tenantId(ApplicationContext.get().getTenantId())
                    .build();

            ApplicationContext.start(ctx);

            User user = new User();
            user.username = dto.username;
            user.email = dto.email;
            user.tenantId = tenantId;
            user = dao.create(user);

            return Response.ok(map(user)).build();
        } finally {
            ApplicationContext.close();
        }
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
        return Response.ok(map(user)).build();
    }

    @GET
    public Response all() {
        UserListDTO dto = new UserListDTO();
        dto.items = dao.findAll().map(this::map).toList();
        return Response.ok(dto).build();
    }

    @DELETE
    public Response deleteAll() {
        dao.deleteAll();
        return Response.ok().build();
    }

    private UserDTO map(User user) {
        UserDTO dto = new UserDTO();
        dto.tenantId = user.tenantId;
        dto.id = user.getId();
        dto.email = user.email;
        dto.username = user.username;
        dto.modificationCount = user.getModificationCount();
        return dto;
    }

}
