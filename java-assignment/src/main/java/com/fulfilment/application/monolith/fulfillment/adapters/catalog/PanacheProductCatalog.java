package com.fulfilment.application.monolith.fulfillment.adapters.catalog;

import com.fulfilment.application.monolith.fulfillment.domain.ports.ProductCatalog;
import com.fulfilment.application.monolith.products.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PanacheProductCatalog implements ProductCatalog {

  @Inject ProductRepository productRepository;

  @Override
  public boolean exists(Long productId) {
    return productRepository.findById(productId) != null;
  }
}
