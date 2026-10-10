package com.fulfilment.application.monolith.stores;

import static io.restassured.http.ContentType.JSON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreLegacySyncTest {

  @InjectMock LegacyStoreManagerGateway legacyStoreManagerGateway;

  @Test
  public void createStoreNotifiesLegacyAfterPersist() {
    RestAssured.given()
        .contentType(JSON)
        .body("{\"name\":\"LEGACY-TEST\",\"quantityProductsInStock\":42}")
        .when()
        .post("/store")
        .then()
        .statusCode(201);

    verify(legacyStoreManagerGateway)
        .createStoreOnLegacySystem(
            argThat(
                store ->
                    "LEGACY-TEST".equals(store.name)
                        && store.quantityProductsInStock == 42
                        && store.id != null));
  }

  @Test
  public void updateStoreNotifiesLegacyWithPersistedEntity() {
    RestAssured.given()
        .contentType(JSON)
        .body("{\"name\":\"KALLAX\",\"quantityProductsInStock\":99}")
        .when()
        .put("/store/2")
        .then()
        .statusCode(200);

    verify(legacyStoreManagerGateway)
        .updateStoreOnLegacySystem(
            argThat(store -> store.id == 2L && store.quantityProductsInStock == 99));
  }

  @Test
  public void failedCreateDoesNotNotifyLegacy() {
    RestAssured.given()
        .contentType(JSON)
        .body("{\"id\":999,\"name\":\"INVALID\",\"quantityProductsInStock\":1}")
        .when()
        .post("/store")
        .then()
        .statusCode(422);

    verify(legacyStoreManagerGateway, never()).createStoreOnLegacySystem(any());
  }
}
