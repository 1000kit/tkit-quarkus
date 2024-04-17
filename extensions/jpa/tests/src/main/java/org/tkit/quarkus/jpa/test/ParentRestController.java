package org.tkit.quarkus.jpa.test;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import java.util.List;

@Path("parent")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional(Transactional.TxType.NOT_SUPPORTED)
@ApplicationScoped
public class ParentRestController {
  @Inject
  ParentDAO parentDAO;
  @GET
  @Path("{id}")
  public Response find(@PathParam("id") String id) {
    Parent tmp = parentDAO.loadById(id);
    if (tmp == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    System.out.println("_____________________________________");
    System.out.println(tmp.getChildren().get(0).getMfeId());
    return Response.ok(tmp).build();
  }

  @GET
  public Response create() {
    Parent tmp = new Parent();
    Child c1 = new Child();
    c1.setMfeId("mfe1");
    Child c2 = new Child();
    c2.setMfeId("mfe2");
    tmp.setChildren(List.of(c1, c2));
//    Sibling sibling = siblingDAO.create(new Sibling());
//    tmp.setSibling(sibling);
    parentDAO.create(tmp);

    return Response.ok(tmp).build();
  }

  @GET
  @Path("page")
  public Response getPageResult() {
    return Response.ok(parentDAO.loadPage().getStream().map(parent -> parent.getChildren().stream().map(TraceableEntity::getId).toList()).toList()).build();
  }
}
