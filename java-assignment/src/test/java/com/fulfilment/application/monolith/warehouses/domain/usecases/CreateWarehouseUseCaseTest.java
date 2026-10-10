package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.warehouses.domain.WarehouseValidator;
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
class CreateWarehouseUseCaseTest {

  @Mock WarehouseStore warehouseStore;
  @Mock LocationResolver locationResolver;

  CreateWarehouseUseCase createWarehouseUseCase;

  @BeforeEach
  void setUp() {
    createWarehouseUseCase =
        new CreateWarehouseUseCase(
            warehouseStore, new WarehouseValidator(warehouseStore, locationResolver));
  }

  @Test
  void createValidWarehousePersists() {
    when(locationResolver.resolveByIdentifier("HELMOND-001"))
        .thenReturn(new Location("HELMOND-001", 1, 45));
    when(warehouseStore.findByBusinessUnitCode("MWH.NEW")).thenReturn(null);
    when(warehouseStore.countActiveByLocation("HELMOND-001")).thenReturn(0L);
    when(warehouseStore.sumActiveCapacityByLocation("HELMOND-001")).thenReturn(0);

    Warehouse request = warehouse("MWH.NEW", "HELMOND-001", 40, 15);
    createWarehouseUseCase.create(request);

    verify(warehouseStore)
        .create(
            argThat(
                saved ->
                    "MWH.NEW".equals(saved.businessUnitCode)
                        && saved.createdAt != null
                        && saved.archivedAt == null));
  }

  @Test
  void createDuplicateBusinessUnitCodeFails() {
    when(locationResolver.resolveByIdentifier("HELMOND-001"))
        .thenReturn(new Location("HELMOND-001", 1, 45));
    when(warehouseStore.findByBusinessUnitCode("MWH.DUP"))
        .thenReturn(warehouse("MWH.DUP", "HELMOND-001", 40, 10));

    assertThrows(
        WebApplicationException.class,
        () -> createWarehouseUseCase.create(warehouse("MWH.DUP", "HELMOND-001", 40, 10)));

    verify(warehouseStore, never()).create(any());
  }

  @Test
  void createInvalidLocationFails() {
    when(locationResolver.resolveByIdentifier("UNKNOWN")).thenReturn(null);

    WebApplicationException error =
        assertThrows(
            WebApplicationException.class,
            () -> createWarehouseUseCase.create(warehouse("MWH.X", "UNKNOWN", 40, 10)));

    assertNotNull(error.getMessage());
    verify(warehouseStore, never()).create(any());
  }

  private static Warehouse warehouse(String bu, String location, int capacity, int stock) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = bu;
    warehouse.location = location;
    warehouse.capacity = capacity;
    warehouse.stock = stock;
    return warehouse;
  }
}
