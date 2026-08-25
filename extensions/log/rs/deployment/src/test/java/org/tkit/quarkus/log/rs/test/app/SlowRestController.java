package org.tkit.quarkus.log.rs.test.app;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("slow")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SlowRestController {

    public static final int DELAY_MS = 3000;

    /**
     * Returns the response itself, so it is already available when the response filter runs.
     */
    @GET
    @Path("response")
    public Response response() throws InterruptedException {
        Thread.sleep(DELAY_MS);
        return Response.ok("OK").build();
    }

    /**
     * Returns a plain entity. The response is created by a later handler of the pipeline, which is
     * skipped once the client dropped the connection, so the response filter sees no response.
     */
    @GET
    @Path("entity")
    public String entity() throws InterruptedException {
        Thread.sleep(DELAY_MS);
        return "OK";
    }

}
