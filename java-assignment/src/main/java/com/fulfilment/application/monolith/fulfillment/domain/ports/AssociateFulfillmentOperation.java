package com.fulfilment.application.monolith.fulfillment.domain.ports;

import com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentAssociation;

public interface AssociateFulfillmentOperation {
  FulfillmentAssociation associate(Long storeId, Long productId, String warehouseBusinessUnitCode);
}
