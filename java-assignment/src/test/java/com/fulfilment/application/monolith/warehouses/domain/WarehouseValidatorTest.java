package com.fulfilment.application.monolith.warehouses.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WarehouseValidatorTest {

  @Mock WarehouseStore warehouseStore;
  @Mock LocationResolver locationResolver;

  WarehouseValidator validator;

  @BeforeEach
  void setUp() {
    validator = new WarehouseValidator(warehouseStore, locationResolver);
  }

  @Test
  void resolveLocationReturnsConfiguredLocation() {
    when(locationResolver.resolveByIdentifier("HELMOND-001"))
        .thenReturn(new Location("HELMOND-001", 1, 45));

    Location location = validator.resolveLocation("HELMOND-001");

    assertEquals("HELMOND-001", location.identification);
  }

  @Test
  void validateForCreationRejectsDuplicateBusinessUnitCode() {
    when(warehouseStore.findByBusinessUnitCode("MWH.DUP")).thenReturn(new Warehouse());
    Location location = new Location("HELMOND-001", 1, 45);

    assertThrows(
        WebApplicationException.class,
        () -> validator.validateForCreation(warehouse("MWH.DUP", "HELMOND-001", 40, 10), location));
  }

  @Test
  void validateLocationConstraintsRejectsStockAboveCapacity() {
    Location location = new Location("HELMOND-001", 1, 45);

    assertThrows(
        WebApplicationException.class,
        () -> validator.validateLocationConstraints(warehouse("MWH.X", "HELMOND-001", 40, 50), location));
  }

  private static Warehouse warehouse(String bu, String loc, int capacity, int stock) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = bu;
    warehouse.location = loc;
    warehouse.capacity = capacity;
    warehouse.stock = stock;
    return warehouse;
  }
}
