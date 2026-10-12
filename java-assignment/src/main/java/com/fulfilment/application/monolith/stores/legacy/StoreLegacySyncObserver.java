package com.fulfilment.application.monolith.stores.legacy;

import com.fulfilment.application.monolith.stores.LegacyStoreManagerGateway;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.stores.events.StoreCreatedEvent;
import com.fulfilment.application.monolith.stores.events.StoreUpdatedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class StoreLegacySyncObserver {

  private static final Logger LOGGER = Logger.getLogger(StoreLegacySyncObserver.class);

  @Inject LegacyStoreManagerGateway legacyStoreManagerGateway;

  void onStoreCreated(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreCreatedEvent event) {
    Store store = Store.findById(event.storeId());
    if (store == null) {
      LOGGER.warnf("Store %s not found after commit; skipping legacy create sync.", event.storeId());
      return;
    }
    legacyStoreManagerGateway.createStoreOnLegacySystem(store);
  }

  void onStoreUpdated(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreUpdatedEvent event) {
    Store store = Store.findById(event.storeId());
    if (store == null) {
      LOGGER.warnf("Store %s not found after commit; skipping legacy update sync.", event.storeId());
      return;
    }
    legacyStoreManagerGateway.updateStoreOnLegacySystem(store);
  }
}
