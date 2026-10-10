package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreEndpointTest {

  @Test
  public void testCrudStore() {
    final String path = "store";

    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(
            containsString("TONSTAD"), containsString("KALLAX"), containsString("BESTÅ"));

    given().when().delete(path + "/1").then().statusCode(204);

    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(not(containsString("TONSTAD")), containsString("KALLAX"), containsString("BESTÅ"));
  }
}
