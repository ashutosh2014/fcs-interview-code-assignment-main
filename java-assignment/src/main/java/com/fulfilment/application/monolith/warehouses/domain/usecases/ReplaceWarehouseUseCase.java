package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.WarehouseValidator;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import java.time.LocalDateTime;
import java.util.Objects;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final WarehouseValidator warehouseValidator;

  public ReplaceWarehouseUseCase(
      WarehouseStore warehouseStore, WarehouseValidator warehouseValidator) {
    this.warehouseStore = warehouseStore;
    this.warehouseValidator = warehouseValidator;
  }

  @Override
  @Transactional
  public void replace(Warehouse newWarehouse) {
    Warehouse previous = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
    if (previous == null) {
      throw new WebApplicationException(
          "No active warehouse found for business unit code "
              + newWarehouse.businessUnitCode
              + ".",
          404);
    }

    if (!Objects.equals(previous.stock, newWarehouse.stock)) {
      throw new WebApplicationException(
          "Replacement warehouse stock must match the previous warehouse stock.", 400);
    }
    if (newWarehouse.capacity == null || newWarehouse.capacity < previous.stock) {
      throw new WebApplicationException(
          "Replacement warehouse capacity must accommodate the previous warehouse stock.", 400);
    }

    var location = warehouseValidator.resolveLocation(newWarehouse.location);

    previous.archivedAt = LocalDateTime.now();
    warehouseStore.update(previous);

    newWarehouse.businessUnitCode = previous.businessUnitCode;
    warehouseValidator.validateForCreation(newWarehouse, location);
    newWarehouse.createdAt = LocalDateTime.now();
    newWarehouse.archivedAt = null;
    warehouseStore.create(newWarehouse);
  }
}
