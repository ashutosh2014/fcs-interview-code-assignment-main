package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentAssociation;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class FulfillmentAssociationRepository
    implements FulfillmentStore, PanacheRepository<DbFulfillmentAssociation> {

  @Override
  public List<FulfillmentAssociation> getAll() {
    return listAll().stream().map(DbFulfillmentAssociation::toDomain).toList();
  }

  @Override
  public List<FulfillmentAssociation> listByStore(Long storeId) {
    return find("storeId", storeId).list().stream().map(DbFulfillmentAssociation::toDomain).toList();
  }

  @Override
  public List<FulfillmentAssociation> listByStoreAndProduct(Long storeId, Long productId) {
    return find("storeId = ?1 and productId = ?2", storeId, productId).list().stream()
        .map(DbFulfillmentAssociation::toDomain)
        .toList();
  }

  @Override
  public List<FulfillmentAssociation> listByWarehouse(String warehouseBusinessUnitCode) {
    return find("warehouseBusinessUnitCode", warehouseBusinessUnitCode).list().stream()
        .map(DbFulfillmentAssociation::toDomain)
        .toList();
  }

  @Override
  public FulfillmentAssociation findByStoreProductAndWarehouse(
      Long storeId, Long productId, String warehouseBusinessUnitCode) {
    return find(
            "storeId = ?1 and productId = ?2 and warehouseBusinessUnitCode = ?3",
            storeId,
            productId,
            warehouseBusinessUnitCode)
        .firstResultOptional()
        .map(DbFulfillmentAssociation::toDomain)
        .orElse(null);
  }

  @Override
  public FulfillmentAssociation getById(Long id) {
    return find("id", id).firstResultOptional().map(DbFulfillmentAssociation::toDomain).orElse(null);
  }

  @Override
  public void create(FulfillmentAssociation association) {
    DbFulfillmentAssociation entity = new DbFulfillmentAssociation();
    entity.storeId = association.storeId;
    entity.productId = association.productId;
    entity.warehouseBusinessUnitCode = association.warehouseBusinessUnitCode;
    persist(entity);
    association.id = entity.id;
  }

  @Override
  public void delete(FulfillmentAssociation association) {
    if (association.id != null) {
      deleteById(association.id);
    }
  }
}
