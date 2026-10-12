package com.fulfilment.application.monolith.fulfillment;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FulfillmentService {

  private final FulfillmentValidator fulfillmentValidator;

  public FulfillmentService(FulfillmentValidator fulfillmentValidator) {
    this.fulfillmentValidator = fulfillmentValidator;
  }

  @Transactional
  public FulfillmentAssociation associate(
      Long storeId, Long productId, String warehouseBusinessUnitCode) {
    fulfillmentValidator.validateAssociateRequest(storeId, productId, warehouseBusinessUnitCode);
    fulfillmentValidator.validateEntitiesExist(storeId, productId, warehouseBusinessUnitCode);

    FulfillmentAssociation existing =
        fulfillmentValidator.findExistingAssociation(storeId, productId, warehouseBusinessUnitCode);
    if (existing != null) {
      return existing;
    }

    fulfillmentValidator.validateAssociationLimits(storeId, productId, warehouseBusinessUnitCode);

    FulfillmentAssociation association = new FulfillmentAssociation();
    association.storeId = storeId;
    association.productId = productId;
    association.warehouseBusinessUnitCode = warehouseBusinessUnitCode;
    association.persist();
    return association;
  }

  @Transactional
  public void remove(Long id) {
    FulfillmentAssociation association = fulfillmentValidator.requireAssociation(id);
    association.delete();
  }
}
