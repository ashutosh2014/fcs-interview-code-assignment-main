package com.fulfilment.application.monolith.fulfillment.adapters.catalog;

import com.fulfilment.application.monolith.fulfillment.domain.ports.StoreCatalog;
import com.fulfilment.application.monolith.stores.Store;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PanacheStoreCatalog implements StoreCatalog {

  @Override
  public boolean exists(Long storeId) {
    return Store.findById(storeId) != null;
  }
}
