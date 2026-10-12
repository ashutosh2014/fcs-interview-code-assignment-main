package com.fulfilment.application.monolith.fulfillment.domain.validators;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.fulfillment.domain.ports.ProductCatalog;
import com.fulfilment.application.monolith.fulfillment.domain.ports.StoreCatalog;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FulfillmentValidatorTest {

  @Mock FulfillmentStore fulfillmentStore;
  @Mock StoreCatalog storeCatalog;
  @Mock ProductCatalog productCatalog;
  @Mock WarehouseStore warehouseStore;

  FulfillmentValidator fulfillmentValidator;

  @BeforeEach
  void setUp() {
    fulfillmentValidator =
        new FulfillmentValidator(fulfillmentStore, storeCatalog, productCatalog, warehouseStore);
  }

  @Test
  void validateAssociateRequestRejectsMissingFields() {
    assertThrows(
        WebApplicationException.class,
        () -> fulfillmentValidator.validateAssociateRequest(null, 1L, "MWH.001"));
  }

  @Test
  void validateAssociateRequestRejectsBlankWarehouseCode() {
    assertThrows(
        WebApplicationException.class,
        () -> fulfillmentValidator.validateAssociateRequest(1L, 1L, "  "));
  }
}
