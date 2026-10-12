package com.fulfilment.application.monolith.fulfillment.domain.ports;

public interface StoreCatalog {
  boolean exists(Long storeId);
}
