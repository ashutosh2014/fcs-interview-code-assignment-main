package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.warehouses.domain.WarehouseValidator;
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
class ArchiveWarehouseUseCaseTest {

  @Mock WarehouseStore warehouseStore;
  @Mock LocationResolver locationResolver;

  ArchiveWarehouseUseCase archiveWarehouseUseCase;

  @BeforeEach
  void setUp() {
    WarehouseValidator warehouseValidator = new WarehouseValidator(warehouseStore, locationResolver);
    archiveWarehouseUseCase = new ArchiveWarehouseUseCase(warehouseStore, warehouseValidator);
  }

  @Test
  void archiveExistingWarehouseSetsArchivedAt() {
    Warehouse active = warehouse("MWH.001", "ZWOLLE-001", 100, 10);
    when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(active);

    Warehouse request = new Warehouse();
    request.businessUnitCode = "MWH.001";
    archiveWarehouseUseCase.archive(request);

    verify(warehouseStore)
        .update(argThat(archived -> archived.archivedAt != null && "MWH.001".equals(archived.businessUnitCode)));
  }

  @Test
  void archiveMissingWarehouseReturns404() {
    when(warehouseStore.findByBusinessUnitCode("MWH.MISSING")).thenReturn(null);

    Warehouse request = new Warehouse();
    request.businessUnitCode = "MWH.MISSING";

    WebApplicationException error =
        assertThrows(WebApplicationException.class, () -> archiveWarehouseUseCase.archive(request));

    assertEquals(404, error.getResponse().getStatus());
    verify(warehouseStore, never()).update(any());
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
