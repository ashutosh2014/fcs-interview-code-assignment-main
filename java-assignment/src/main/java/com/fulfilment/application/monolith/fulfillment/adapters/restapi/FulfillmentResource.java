package com.fulfilment.application.monolith.fulfillment.adapters.restapi;

import com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentAssociation;
import com.fulfilment.application.monolith.fulfillment.domain.ports.AssociateFulfillmentOperation;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.fulfillment.domain.ports.RemoveFulfillmentOperation;
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

  @Inject FulfillmentStore fulfillmentStore;
  @Inject AssociateFulfillmentOperation associateFulfillmentOperation;
  @Inject RemoveFulfillmentOperation removeFulfillmentOperation;

  @GET
  public List<FulfillmentAssociation> list(@QueryParam("storeId") Long storeId) {
    if (storeId == null) {
      return fulfillmentStore.getAll();
    }
    return fulfillmentStore.listByStore(storeId);
  }

  @POST
  public Response create(FulfillmentAssociation request) {
    FulfillmentAssociation created =
        associateFulfillmentOperation.associate(
            request.storeId, request.productId, request.warehouseBusinessUnitCode);
    return Response.status(201).entity(created).build();
  }

  @DELETE
  @Path("{id}")
  public Response delete(Long id) {
    removeFulfillmentOperation.remove(id);
    return Response.status(204).build();
  }
}
