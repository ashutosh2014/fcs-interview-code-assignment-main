package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentAssociation;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.fulfillment.domain.ports.ProductCatalog;
import com.fulfilment.application.monolith.fulfillment.domain.ports.StoreCatalog;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.TestTransaction;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AssociateFulfillmentUseCaseTest {

  @Inject AssociateFulfillmentUseCase associateFulfillmentUseCase;

  @Inject FulfillmentStore fulfillmentStore;

  @InjectMock WarehouseStore warehouseStore;

  @InjectMock ProductCatalog productCatalog;

  @InjectMock StoreCatalog storeCatalog;

  @BeforeEach
  void stubCatalogs() {
    when(storeCatalog.exists(1L)).thenReturn(true);
    when(storeCatalog.exists(2L)).thenReturn(true);
    when(storeCatalog.exists(3L)).thenReturn(true);
    when(storeCatalog.exists(999L)).thenReturn(false);
    when(productCatalog.exists(1L)).thenReturn(true);
    when(productCatalog.exists(2L)).thenReturn(true);
    when(productCatalog.exists(3L)).thenReturn(true);
    when(productCatalog.exists(999L)).thenReturn(false);
  }

  @Test
  @TestTransaction
  void associatePersistsWhenConstraintsAllow() {
    when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(activeWarehouse("MWH.001"));

    FulfillmentAssociation created = associateFulfillmentUseCase.associate(1L, 1L, "MWH.001");

    assertEquals(1L, created.storeId);
    assertEquals(1L, created.productId);
    assertEquals("MWH.001", created.warehouseBusinessUnitCode);
  }

  @Test
  void associateRejectsUnknownStore() {
    assertThrows(
        WebApplicationException.class, () -> associateFulfillmentUseCase.associate(999L, 1L, "MWH.001"));
  }

  @Test
  void associateRejectsUnknownProduct() {
    when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(activeWarehouse("MWH.001"));

    assertThrows(
        WebApplicationException.class, () -> associateFulfillmentUseCase.associate(1L, 999L, "MWH.001"));
  }

  @Test
  void associateRejectsUnknownWarehouse() {
    when(warehouseStore.findByBusinessUnitCode("MWH.MISSING")).thenReturn(null);

    assertThrows(
        WebApplicationException.class,
        () -> associateFulfillmentUseCase.associate(1L, 1L, "MWH.MISSING"));
  }

  @Test
  @TestTransaction
  void associateIsIdempotentForSameTriple() {
    when(warehouseStore.findByBusinessUnitCode("MWH.023")).thenReturn(activeWarehouse("MWH.023"));

    FulfillmentAssociation first = associateFulfillmentUseCase.associate(3L, 3L, "MWH.023");
    FulfillmentAssociation second = associateFulfillmentUseCase.associate(3L, 3L, "MWH.023");

    assertEquals(first.id, second.id);
  }

  @Test
  @TestTransaction
  void associateRejectsThirdWarehouseForProductAtStore() {
    when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(activeWarehouse("MWH.001"));
    when(warehouseStore.findByBusinessUnitCode("MWH.012")).thenReturn(activeWarehouse("MWH.012"));
    when(warehouseStore.findByBusinessUnitCode("MWH.023")).thenReturn(activeWarehouse("MWH.023"));

    associateFulfillmentUseCase.associate(2L, 2L, "MWH.001");
    associateFulfillmentUseCase.associate(2L, 2L, "MWH.012");

    assertThrows(
        WebApplicationException.class, () -> associateFulfillmentUseCase.associate(2L, 2L, "MWH.023"));
  }

  private static Warehouse activeWarehouse(String code) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = code;
    return warehouse;
  }
}
