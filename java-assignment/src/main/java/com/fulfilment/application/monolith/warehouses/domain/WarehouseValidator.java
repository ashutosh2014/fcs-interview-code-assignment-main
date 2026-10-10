package com.fulfilment.application.monolith.warehouses.domain;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class WarehouseValidator {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public WarehouseValidator(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  public Location resolveLocation(String locationId) {
    if (locationId == null || locationId.isBlank()) {
      throw new WebApplicationException("Warehouse location is required.", 400);
    }
    Location location = locationResolver.resolveByIdentifier(locationId);
    if (location == null) {
      throw new WebApplicationException("Invalid warehouse location.", 400);
    }
    return location;
  }

  public void validateForCreation(Warehouse warehouse, Location location) {
    if (warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank()) {
      throw new WebApplicationException("Business unit code is required.", 400);
    }
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new WebApplicationException("Business unit code already exists.", 400);
    }
    validateLocationConstraints(warehouse, location);
  }

  public void validateLocationConstraints(Warehouse warehouse, Location location) {
    if (warehouse.capacity == null || warehouse.stock == null) {
      throw new WebApplicationException("Capacity and stock are required.", 400);
    }
    if (warehouse.stock < 0 || warehouse.capacity < 0) {
      throw new WebApplicationException("Capacity and stock must be non-negative.", 400);
    }
    if (warehouse.stock > warehouse.capacity) {
      throw new WebApplicationException("Stock cannot exceed warehouse capacity.", 400);
    }
    if (warehouse.capacity > location.maxCapacity) {
      throw new WebApplicationException(
          "Warehouse capacity exceeds maximum capacity for the location.", 400);
    }

    long warehousesAtLocation = warehouseStore.countActiveByLocation(location.identification);
    if (warehousesAtLocation >= location.maxNumberOfWarehouses) {
      throw new WebApplicationException(
          "Maximum number of warehouses reached for this location.", 400);
    }

    int totalCapacity =
        warehouseStore.sumActiveCapacityByLocation(location.identification) + warehouse.capacity;
    if (totalCapacity > location.maxCapacity) {
      throw new WebApplicationException(
          "Total warehouse capacity at location would exceed the location limit.", 400);
    }
  }
}
