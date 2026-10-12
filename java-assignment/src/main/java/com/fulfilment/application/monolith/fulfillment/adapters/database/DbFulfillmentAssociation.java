package com.fulfilment.application.monolith.fulfillment.adapters.database;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Cacheable
@Table(
    name = "FulfillmentAssociation",
    uniqueConstraints =
        @UniqueConstraint(
            columnNames = {"store_id", "product_id", "warehouse_business_unit_code"}))
public class DbFulfillmentAssociation extends PanacheEntity {

  @Column(name = "store_id", nullable = false)
  public Long storeId;

  @Column(name = "product_id", nullable = false)
  public Long productId;

  @Column(name = "warehouse_business_unit_code", nullable = false, length = 40)
  public String warehouseBusinessUnitCode;

  static com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentAssociation toDomain(
      DbFulfillmentAssociation entity) {
    var association =
        new com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentAssociation();
    association.id = entity.id;
    association.storeId = entity.storeId;
    association.productId = entity.productId;
    association.warehouseBusinessUnitCode = entity.warehouseBusinessUnitCode;
    return association;
  }
}
