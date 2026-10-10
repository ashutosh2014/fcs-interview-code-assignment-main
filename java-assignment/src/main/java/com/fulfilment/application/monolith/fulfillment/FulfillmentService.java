package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class FulfillmentService {

  private static final int MAX_WAREHOUSES_PER_PRODUCT_PER_STORE = 2;
  private static final int MAX_WAREHOUSES_PER_STORE = 3;
  private static final int MAX_PRODUCT_TYPES_PER_WAREHOUSE = 5;

  private final WarehouseStore warehouseStore;
  private final ProductRepository productRepository;

  public FulfillmentService(WarehouseStore warehouseStore, ProductRepository productRepository) {
    this.warehouseStore = warehouseStore;
    this.productRepository = productRepository;
  }

  @Transactional
  public FulfillmentAssociation associate(
      Long storeId, Long productId, String warehouseBusinessUnitCode) {
    if (storeId == null || productId == null || warehouseBusinessUnitCode == null) {
      throw new WebApplicationException("storeId, productId and warehouseBusinessUnitCode are required.", 400);
    }
    if (warehouseBusinessUnitCode.isBlank()) {
      throw new WebApplicationException("warehouseBusinessUnitCode is required.", 400);
    }

    if (Store.findById(storeId) == null) {
      throw new WebApplicationException("Store with id of " + storeId + " does not exist.", 404);
    }
    if (productRepository.findById(productId) == null) {
      throw new WebApplicationException("Product with id of " + productId + " does not exist.", 404);
    }
    if (warehouseStore.findByBusinessUnitCode(warehouseBusinessUnitCode) == null) {
      throw new WebApplicationException(
          "Warehouse with business unit code of " + warehouseBusinessUnitCode + " does not exist.",
          404);
    }

    FulfillmentAssociation existing =
        FulfillmentAssociation.find(
                "storeId = ?1 and productId = ?2 and warehouseBusinessUnitCode = ?3",
                storeId,
                productId,
                warehouseBusinessUnitCode)
            .firstResult();
    if (existing != null) {
      return existing;
    }

    List<FulfillmentAssociation> forStoreProduct =
        FulfillmentAssociation.list("storeId = ?1 and productId = ?2", storeId, productId);
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

    List<FulfillmentAssociation> forStore =
        FulfillmentAssociation.list("storeId = ?1", storeId);
    Set<String> warehousesForStore =
        forStore.stream().map(link -> link.warehouseBusinessUnitCode).collect(Collectors.toSet());
    if (!warehousesForStore.contains(warehouseBusinessUnitCode)
        && warehousesForStore.size() >= MAX_WAREHOUSES_PER_STORE) {
      throw new WebApplicationException(
          "A store can be fulfilled by at most " + MAX_WAREHOUSES_PER_STORE + " warehouses.", 400);
    }

    List<FulfillmentAssociation> forWarehouse =
        FulfillmentAssociation.list("warehouseBusinessUnitCode = ?1", warehouseBusinessUnitCode);
    Set<Long> productsForWarehouse =
        forWarehouse.stream().map(link -> link.productId).collect(Collectors.toSet());
    if (!productsForWarehouse.contains(productId)
        && productsForWarehouse.size() >= MAX_PRODUCT_TYPES_PER_WAREHOUSE) {
      throw new WebApplicationException(
          "A warehouse can store at most " + MAX_PRODUCT_TYPES_PER_WAREHOUSE + " product types.",
          400);
    }

    FulfillmentAssociation association = new FulfillmentAssociation();
    association.storeId = storeId;
    association.productId = productId;
    association.warehouseBusinessUnitCode = warehouseBusinessUnitCode;
    association.persist();
    return association;
  }

  @Transactional
  public void remove(Long id) {
    FulfillmentAssociation association = FulfillmentAssociation.findById(id);
    if (association == null) {
      throw new WebApplicationException("Fulfillment association with id of " + id + " does not exist.", 404);
    }
    association.delete();
  }
}
