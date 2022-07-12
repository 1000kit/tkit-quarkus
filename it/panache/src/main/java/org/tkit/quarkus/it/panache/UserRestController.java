package org.tkit.quarkus.it.panache;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.tkit.quarkus.log.cdi.LogService;

@LogService
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
        dto.id = user.getId();
        dto.username = user.username;
        dto.email = user.email;
        return Response.ok(dto).build();
    }

    @POST
    @Transactional
    public Response create(UserDTO dto) {
        User user = new User();
        user.username = dto.username;
        user.email = dto.email;
        dao.persist(user);

        return Response.ok(user).build();
    }
}
