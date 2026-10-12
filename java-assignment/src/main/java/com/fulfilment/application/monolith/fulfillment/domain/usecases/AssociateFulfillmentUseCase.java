package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentAssociation;
import com.fulfilment.application.monolith.fulfillment.domain.ports.AssociateFulfillmentOperation;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.fulfillment.domain.validators.FulfillmentValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AssociateFulfillmentUseCase implements AssociateFulfillmentOperation {

  private final FulfillmentValidator fulfillmentValidator;
  private final FulfillmentStore fulfillmentStore;

  public AssociateFulfillmentUseCase(
      FulfillmentValidator fulfillmentValidator, FulfillmentStore fulfillmentStore) {
    this.fulfillmentValidator = fulfillmentValidator;
    this.fulfillmentStore = fulfillmentStore;
  }

  @Override
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
    fulfillmentStore.create(association);
    return association;
  }
}
