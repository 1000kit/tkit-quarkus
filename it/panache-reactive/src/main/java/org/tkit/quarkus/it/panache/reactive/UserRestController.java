package org.tkit.quarkus.it.panache.reactive;

import static jakarta.ws.rs.core.Response.Status.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;

@Path("users")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserRestController {

    @Inject
    UserDAO dao;

    @GET
    @Path("{id}")
    @WithSession
    public Uni<Response> load(String id) {
        return dao.findById(id)
                .onItem().ifNotNull().transform(d -> Response.ok(dto(d)).build())
                .onItem().ifNull().continueWith(Response.ok().status(NOT_FOUND)::build);
    }

    @POST
    @WithTransaction
    public Uni<Response> create(UserDTO dto) {
        return dao.persist(map(dto))
                .onItem().ifNotNull().transform(e -> Response.ok(dto(e)).status(CREATED).build())
                .onItem().ifNull().continueWith(Response.ok().status(INTERNAL_SERVER_ERROR)::build);
    }

    private static UserDTO dto(User e) {
        UserDTO dto = new UserDTO();
        dto.id = e.getId();
        dto.username = e.username;
        dto.email = e.email;
        return dto;
    }

    private static User map(UserDTO dto) {
        User user = new User();
        user.username = dto.username;
        user.email = dto.email;
        return user;
    }
}
