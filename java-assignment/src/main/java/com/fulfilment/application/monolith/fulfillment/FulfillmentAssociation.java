package com.fulfilment.application.monolith.fulfillment;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Cacheable
@Table(
    uniqueConstraints =
        @UniqueConstraint(
            columnNames = {"store_id", "product_id", "warehouse_business_unit_code"}))
public class FulfillmentAssociation extends PanacheEntity {

  @Column(name = "store_id", nullable = false)
  public Long storeId;

  @Column(name = "product_id", nullable = false)
  public Long productId;

  @Column(name = "warehouse_business_unit_code", nullable = false, length = 40)
  public String warehouseBusinessUnitCode;
}
