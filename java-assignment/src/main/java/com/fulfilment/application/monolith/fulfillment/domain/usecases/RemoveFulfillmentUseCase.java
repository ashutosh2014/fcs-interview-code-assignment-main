package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.fulfillment.domain.ports.RemoveFulfillmentOperation;
import com.fulfilment.application.monolith.fulfillment.domain.validators.FulfillmentValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RemoveFulfillmentUseCase implements RemoveFulfillmentOperation {

  private final FulfillmentValidator fulfillmentValidator;
  private final FulfillmentStore fulfillmentStore;

  public RemoveFulfillmentUseCase(
      FulfillmentValidator fulfillmentValidator, FulfillmentStore fulfillmentStore) {
    this.fulfillmentValidator = fulfillmentValidator;
    this.fulfillmentStore = fulfillmentStore;
  }

  @Override
  @Transactional
  public void remove(Long id) {
    var association = fulfillmentValidator.requireAssociation(id);
    fulfillmentStore.delete(association);
  }
}
