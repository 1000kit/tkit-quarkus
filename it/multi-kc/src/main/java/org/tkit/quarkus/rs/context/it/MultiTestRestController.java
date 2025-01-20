package org.tkit.quarkus.rs.context.it;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.tkit.quarkus.log.cdi.LogService;

@Path("test1")
@LogService
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MultiTestRestController {

    @Inject
    JsonWebToken accessToken;

    @GET
    public Response test1() {
        return Response.ok(accessToken.getIssuer()).build();
    }

    @GET
    @Path("public")
    public Response public1() {
        return Response.ok(accessToken.getIssuer()).build();
    }
}
