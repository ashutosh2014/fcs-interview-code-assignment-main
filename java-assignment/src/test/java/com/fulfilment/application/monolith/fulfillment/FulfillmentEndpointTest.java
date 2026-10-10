package com.fulfilment.application.monolith.fulfillment;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class FulfillmentEndpointTest {

  @Test
  void associateWarehouseToProductForStore() {
    given()
        .contentType(JSON)
        .body(
            """
            {
              "storeId": 1,
              "productId": 1,
              "warehouseBusinessUnitCode": "MWH.012"
            }
            """)
        .when()
        .post("/fulfillment")
        .then()
        .statusCode(201)
        .body(containsString("MWH.012"), containsString("\"storeId\":1"));
  }

  @Test
  void rejectThirdWarehouseForProductAtStore() {
    given()
        .contentType(JSON)
        .body("{\"storeId\":2,\"productId\":2,\"warehouseBusinessUnitCode\":\"MWH.001\"}")
        .post("/fulfillment")
        .then()
        .statusCode(201);
    given()
        .contentType(JSON)
        .body("{\"storeId\":2,\"productId\":2,\"warehouseBusinessUnitCode\":\"MWH.012\"}")
        .post("/fulfillment")
        .then()
        .statusCode(201);
    given()
        .contentType(JSON)
        .body("{\"storeId\":2,\"productId\":2,\"warehouseBusinessUnitCode\":\"MWH.023\"}")
        .post("/fulfillment")
        .then()
        .statusCode(400);
  }
}
