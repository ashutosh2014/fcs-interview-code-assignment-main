package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.TestTransaction;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StoreServiceTest {

  @Inject StoreService storeService;

  @Test
  @TestTransaction
  void createPersistsStore() {
    Store store = new Store();
    store.name = "CI-STORE";

    Store created = storeService.create(store);

    assertEquals("CI-STORE", created.name);
    assertEquals(0, created.quantityProductsInStock);
  }

  @Test
  @TestTransaction
  void createWithPresetIdRejected() {
    Store store = new Store();
    store.id = 99L;
    store.name = "INVALID";

    assertThrows(WebApplicationException.class, () -> storeService.create(store));
  }

  @Test
  @TestTransaction
  void updateChangesStoreFields() {
    Store created = storeService.create(storeUpdate("BEFORE-UPDATE", 1));

    Store updated = storeService.update(created.id, storeUpdate("AFTER-UPDATE", 12));

    assertEquals("AFTER-UPDATE", updated.name);
    assertEquals(12, updated.quantityProductsInStock);
  }

  @Test
  @TestTransaction
  void patchUpdatesProvidedFields() {
    Store created = storeService.create(storeUpdate("PATCH-ME", 10));

    Store patched = storeService.patch(created.id, storeUpdate("PATCHED", 20));

    assertEquals("PATCHED", patched.name);
    assertEquals(20, patched.quantityProductsInStock);
  }

  @Test
  @TestTransaction
  void deleteRemovesStore() {
    Store created = storeService.create(storeUpdate("TO-DELETE", 3));

    storeService.delete(created.id);

    assertThrows(
        WebApplicationException.class,
        () -> storeService.update(created.id, storeUpdate("X", 1)));
  }

  @Test
  void updateMissingStoreReturns404() {
    assertThrows(
        WebApplicationException.class, () -> storeService.update(999L, storeUpdate("X", 1)));
  }

  @Test
  void updateWithoutNameReturns422() {
    Store store = new Store();
    store.quantityProductsInStock = 1;

    assertThrows(WebApplicationException.class, () -> storeService.update(1L, store));
  }

  private static Store storeUpdate(String name, int stock) {
    Store store = new Store();
    store.name = name;
    store.quantityProductsInStock = stock;
    return store;
  }
}
