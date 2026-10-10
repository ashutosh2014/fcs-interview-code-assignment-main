package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
class ReplaceWarehouseUseCaseTest {

  @Mock WarehouseStore warehouseStore;
  @Mock LocationResolver locationResolver;

  ReplaceWarehouseUseCase replaceWarehouseUseCase;

  @BeforeEach
  void setUp() {
    replaceWarehouseUseCase =
        new ReplaceWarehouseUseCase(
            warehouseStore, new WarehouseValidator(warehouseStore, locationResolver));
  }

  @Test
  void replaceValidWarehouseArchivesPreviousAndCreatesNew() {
    Warehouse previous = warehouse("MWH.012", "AMSTERDAM-001", 50, 5);
    when(warehouseStore.findByBusinessUnitCode("MWH.012")).thenReturn(previous, (Warehouse) null);
    when(locationResolver.resolveByIdentifier("AMSTERDAM-001"))
        .thenReturn(new Location("AMSTERDAM-001", 5, 100));
    when(warehouseStore.countActiveByLocation("AMSTERDAM-001")).thenReturn(0L);
    when(warehouseStore.sumActiveCapacityByLocation("AMSTERDAM-001")).thenReturn(0);

    Warehouse replacement = warehouse("MWH.012", "AMSTERDAM-001", 50, 5);
    replaceWarehouseUseCase.replace(replacement);

    verify(warehouseStore).update(previous);
    verify(warehouseStore).create(replacement);
  }

  @Test
  void replaceStockMismatchReturns400() {
    Warehouse previous = warehouse("MWH.012", "AMSTERDAM-001", 50, 5);
    when(warehouseStore.findByBusinessUnitCode("MWH.012")).thenReturn(previous);

    Warehouse replacement = warehouse("MWH.012", "AMSTERDAM-001", 50, 99);

    WebApplicationException error =
        assertThrows(WebApplicationException.class, () -> replaceWarehouseUseCase.replace(replacement));

    assertEquals(400, error.getResponse().getStatus());
    verify(warehouseStore, never()).update(any());
    verify(warehouseStore, never()).create(any());
  }

  @Test
  void replaceMissingActiveWarehouseReturns404() {
    when(warehouseStore.findByBusinessUnitCode("MWH.MISSING")).thenReturn(null);

    WebApplicationException error =
        assertThrows(
            WebApplicationException.class,
            () -> replaceWarehouseUseCase.replace(warehouse("MWH.MISSING", "AMSTERDAM-001", 50, 5)));

    assertEquals(404, error.getResponse().getStatus());
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
