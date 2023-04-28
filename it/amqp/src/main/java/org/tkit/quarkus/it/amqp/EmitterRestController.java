package org.tkit.quarkus.it.amqp;

import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;

import io.smallrye.reactive.messaging.amqp.OutgoingAmqpMetadata;
import io.vertx.core.json.JsonObject;

@Path("emitter")
public class EmitterRestController {

    @Inject
    @Channel("test")
    Emitter<String> emitter;

    @GET
    public Response send() {

        JsonObject json = new JsonObject();
        json.put("hello", "world");
        json.put("some", "content");

        OutgoingAmqpMetadata metadata = OutgoingAmqpMetadata.builder()
                .withApplicationProperties(json)
                .withApplicationProperty("TEST", "KEY")
                .build();

        Message<String> message = Message.of(UUID.randomUUID().toString(), Metadata.of(metadata));
        emitter.send(message);
        return Response.ok(message).build();
    }

}
