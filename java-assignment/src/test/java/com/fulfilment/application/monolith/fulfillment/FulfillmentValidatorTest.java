package com.fulfilment.application.monolith.fulfillment;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FulfillmentValidatorTest {

  @Mock WarehouseStore warehouseStore;
  @Mock ProductRepository productRepository;

  FulfillmentValidator fulfillmentValidator;

  @BeforeEach
  void setUp() {
    fulfillmentValidator = new FulfillmentValidator(warehouseStore, productRepository);
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
