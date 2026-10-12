package com.fulfilment.application.monolith.fulfillment.domain.ports;

public interface ProductCatalog {
  boolean exists(Long productId);
}
