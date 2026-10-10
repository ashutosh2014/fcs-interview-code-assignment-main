package com.fulfilment.application.monolith.fulfillment;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("fulfillment")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FulfillmentResource {

  @Inject FulfillmentService fulfillmentService;

  @GET
  public List<FulfillmentAssociation> list(@QueryParam("storeId") Long storeId) {
    if (storeId == null) {
      return FulfillmentAssociation.listAll();
    }
    return FulfillmentAssociation.list("storeId = ?1", storeId);
  }

  @POST
  public Response create(FulfillmentAssociation request) {
    FulfillmentAssociation created =
        fulfillmentService.associate(
            request.storeId, request.productId, request.warehouseBusinessUnitCode);
    return Response.status(201).entity(created).build();
  }

  @DELETE
  @Path("{id}")
  public Response delete(Long id) {
    fulfillmentService.remove(id);
    return Response.status(204).build();
  }
}
