package com.fulfilment.application.monolith.fulfillment.domain.ports;

import com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentAssociation;
import java.util.List;

public interface FulfillmentStore {

  List<FulfillmentAssociation> getAll();

  List<FulfillmentAssociation> listByStore(Long storeId);

  List<FulfillmentAssociation> listByStoreAndProduct(Long storeId, Long productId);

  List<FulfillmentAssociation> listByWarehouse(String warehouseBusinessUnitCode);

  FulfillmentAssociation findByStoreProductAndWarehouse(
      Long storeId, Long productId, String warehouseBusinessUnitCode);

  FulfillmentAssociation getById(Long id);

  void create(FulfillmentAssociation association);

  void delete(FulfillmentAssociation association);
}
