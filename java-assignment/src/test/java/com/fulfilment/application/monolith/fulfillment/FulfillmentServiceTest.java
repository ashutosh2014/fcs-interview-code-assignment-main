package com.fulfilment.application.monolith.fulfillment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.TestTransaction;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

@QuarkusTest
class FulfillmentServiceTest {

  @Inject FulfillmentService fulfillmentService;

  @InjectMock WarehouseStore warehouseStore;

  @InjectMock ProductRepository productRepository;

  @Test
  @TestTransaction
  void associatePersistsWhenConstraintsAllow() {
    when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(activeWarehouse("MWH.001"));
    when(productRepository.findById(1L)).thenReturn(product(1L));

    FulfillmentAssociation created = fulfillmentService.associate(1L, 1L, "MWH.001");

    assertEquals(1L, created.storeId);
    assertEquals(1L, created.productId);
    assertEquals("MWH.001", created.warehouseBusinessUnitCode);
  }

  @Test
  void associateRejectsUnknownStore() {
    assertThrows(WebApplicationException.class, () -> fulfillmentService.associate(999L, 1L, "MWH.001"));
  }

  @Test
  void associateRejectsUnknownProduct() {
    when(productRepository.findById(999L)).thenReturn(null);
    assertThrows(WebApplicationException.class, () -> fulfillmentService.associate(1L, 999L, "MWH.001"));
  }

  @Test
  void associateRejectsUnknownWarehouse() {
    when(productRepository.findById(1L)).thenReturn(product(1L));
    when(warehouseStore.findByBusinessUnitCode("MWH.MISSING")).thenReturn(null);
    assertThrows(
        WebApplicationException.class, () -> fulfillmentService.associate(1L, 1L, "MWH.MISSING"));
  }

  @Test
  @TestTransaction
  void associateIsIdempotentForSameTriple() {
    when(warehouseStore.findByBusinessUnitCode("MWH.023")).thenReturn(activeWarehouse("MWH.023"));
    when(productRepository.findById(3L)).thenReturn(product(3L));

    FulfillmentAssociation first = fulfillmentService.associate(3L, 3L, "MWH.023");
    FulfillmentAssociation second = fulfillmentService.associate(3L, 3L, "MWH.023");

    assertEquals(first.id, second.id);
  }

  @Test
  @TestTransaction
  void associateRejectsThirdWarehouseForProductAtStore() {
    when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(activeWarehouse("MWH.001"));
    when(warehouseStore.findByBusinessUnitCode("MWH.012")).thenReturn(activeWarehouse("MWH.012"));
    when(warehouseStore.findByBusinessUnitCode("MWH.023")).thenReturn(activeWarehouse("MWH.023"));
    when(productRepository.findById(2L)).thenReturn(product(2L));

    fulfillmentService.associate(2L, 2L, "MWH.001");
    fulfillmentService.associate(2L, 2L, "MWH.012");

    assertThrows(
        WebApplicationException.class, () -> fulfillmentService.associate(2L, 2L, "MWH.023"));
  }

  @Test
  @TestTransaction
  void removeDeletesAssociation() {
    when(warehouseStore.findByBusinessUnitCode("MWH.012")).thenReturn(activeWarehouse("MWH.012"));
    when(productRepository.findById(2L)).thenReturn(product(2L));
    FulfillmentAssociation created = fulfillmentService.associate(2L, 2L, "MWH.012");

    fulfillmentService.remove(created.id);

    assertEquals(null, FulfillmentAssociation.findById(created.id));
  }

  private static Warehouse activeWarehouse(String code) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = code;
    return warehouse;
  }

  private static Product product(Long id) {
    Product product = new Product();
    product.id = id;
    product.name = "P" + id;
    return product;
  }
}
