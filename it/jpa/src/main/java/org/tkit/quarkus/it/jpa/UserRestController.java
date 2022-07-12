package org.tkit.quarkus.it.jpa;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.tkit.quarkus.log.rs.RestService;

@RestService(configKey = "user")
@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserRestController extends TestAbstract implements TestInterface {

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
    @RestService(configKey = "create")
    public Response create(UserDTO dto) {
        User user = new User();
        user.username = dto.username;
        user.email = dto.email;
        dao.create(user);

        return Response.ok(user).build();
    }

    @GET
    @Path("ping2")
    public Response ping2() {
        return Response.ok().build();
    }
}
