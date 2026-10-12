package com.fulfilment.application.monolith.fulfillment.domain.validators;

import com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentAssociation;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.fulfillment.domain.ports.ProductCatalog;
import com.fulfilment.application.monolith.fulfillment.domain.ports.StoreCatalog;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class FulfillmentValidator {

  public static final int MAX_WAREHOUSES_PER_PRODUCT_PER_STORE = 2;
  public static final int MAX_WAREHOUSES_PER_STORE = 3;
  public static final int MAX_PRODUCT_TYPES_PER_WAREHOUSE = 5;

  private final FulfillmentStore fulfillmentStore;
  private final StoreCatalog storeCatalog;
  private final ProductCatalog productCatalog;
  private final WarehouseStore warehouseStore;

  public FulfillmentValidator(
      FulfillmentStore fulfillmentStore,
      StoreCatalog storeCatalog,
      ProductCatalog productCatalog,
      WarehouseStore warehouseStore) {
    this.fulfillmentStore = fulfillmentStore;
    this.storeCatalog = storeCatalog;
    this.productCatalog = productCatalog;
    this.warehouseStore = warehouseStore;
  }

  public void validateAssociateRequest(Long storeId, Long productId, String warehouseBusinessUnitCode) {
    if (storeId == null || productId == null || warehouseBusinessUnitCode == null) {
      throw new WebApplicationException(
          "storeId, productId and warehouseBusinessUnitCode are required.", 400);
    }
    if (warehouseBusinessUnitCode.isBlank()) {
      throw new WebApplicationException("warehouseBusinessUnitCode is required.", 400);
    }
  }

  public void validateEntitiesExist(Long storeId, Long productId, String warehouseBusinessUnitCode) {
    if (!storeCatalog.exists(storeId)) {
      throw new WebApplicationException("Store with id of " + storeId + " does not exist.", 404);
    }
    if (!productCatalog.exists(productId)) {
      throw new WebApplicationException("Product with id of " + productId + " does not exist.", 404);
    }
    if (warehouseStore.findByBusinessUnitCode(warehouseBusinessUnitCode) == null) {
      throw new WebApplicationException(
          "Warehouse with business unit code of " + warehouseBusinessUnitCode + " does not exist.",
          404);
    }
  }

  public FulfillmentAssociation findExistingAssociation(
      Long storeId, Long productId, String warehouseBusinessUnitCode) {
    return fulfillmentStore.findByStoreProductAndWarehouse(
        storeId, productId, warehouseBusinessUnitCode);
  }

  public void validateAssociationLimits(
      Long storeId, Long productId, String warehouseBusinessUnitCode) {
    List<FulfillmentAssociation> forStoreProduct =
        fulfillmentStore.listByStoreAndProduct(storeId, productId);
    Set<String> warehousesForStoreProduct =
        forStoreProduct.stream()
            .map(link -> link.warehouseBusinessUnitCode)
            .collect(Collectors.toSet());
    if (warehousesForStoreProduct.size() >= MAX_WAREHOUSES_PER_PRODUCT_PER_STORE) {
      throw new WebApplicationException(
          "A product can be fulfilled by at most "
              + MAX_WAREHOUSES_PER_PRODUCT_PER_STORE
              + " warehouses per store.",
          400);
    }

    List<FulfillmentAssociation> forStore = fulfillmentStore.listByStore(storeId);
    Set<String> warehousesForStore =
        forStore.stream().map(link -> link.warehouseBusinessUnitCode).collect(Collectors.toSet());
    if (!warehousesForStore.contains(warehouseBusinessUnitCode)
        && warehousesForStore.size() >= MAX_WAREHOUSES_PER_STORE) {
      throw new WebApplicationException(
          "A store can be fulfilled by at most " + MAX_WAREHOUSES_PER_STORE + " warehouses.", 400);
    }

    List<FulfillmentAssociation> forWarehouse =
        fulfillmentStore.listByWarehouse(warehouseBusinessUnitCode);
    Set<Long> productsForWarehouse =
        forWarehouse.stream().map(link -> link.productId).collect(Collectors.toSet());
    if (!productsForWarehouse.contains(productId)
        && productsForWarehouse.size() >= MAX_PRODUCT_TYPES_PER_WAREHOUSE) {
      throw new WebApplicationException(
          "A warehouse can store at most " + MAX_PRODUCT_TYPES_PER_WAREHOUSE + " product types.",
          400);
    }
  }

  public FulfillmentAssociation requireAssociation(Long id) {
    FulfillmentAssociation association = fulfillmentStore.getById(id);
    if (association == null) {
      throw new WebApplicationException(
          "Fulfillment association with id of " + id + " does not exist.", 404);
    }
    return association;
  }
}
