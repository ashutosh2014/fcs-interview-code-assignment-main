package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RemoveFulfillmentUseCaseTest {

  @Inject AssociateFulfillmentUseCase associateFulfillmentUseCase;
  @Inject RemoveFulfillmentUseCase removeFulfillmentUseCase;
  @Inject FulfillmentStore fulfillmentStore;

  @InjectMock WarehouseStore warehouseStore;
  @InjectMock ProductCatalog productCatalog;
  @InjectMock StoreCatalog storeCatalog;

  @BeforeEach
  void stubCatalogs() {
    when(storeCatalog.exists(2L)).thenReturn(true);
    when(productCatalog.exists(2L)).thenReturn(true);
  }

  @Test
  @TestTransaction
  void removeDeletesAssociation() {
    when(warehouseStore.findByBusinessUnitCode("MWH.012")).thenReturn(activeWarehouse("MWH.012"));
    FulfillmentAssociation created = associateFulfillmentUseCase.associate(2L, 2L, "MWH.012");

    removeFulfillmentUseCase.remove(created.id);

    assertNull(fulfillmentStore.getById(created.id));
  }

  private static Warehouse activeWarehouse(String code) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = code;
    return warehouse;
  }
}
