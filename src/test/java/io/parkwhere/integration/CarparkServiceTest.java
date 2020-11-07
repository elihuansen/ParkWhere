package io.parkwhere.integration;

import io.parkwhere.MainVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Carpark Service Test")
public class CarparkServiceTest extends BaseServiceTest {

    private static final String CARPARK_ENDPOINT = "/carpark";

    @Override
    public Class<? extends AbstractVerticle> getVerticleClass() {
        return MainVerticle.class;
    }

    @Test
    @DisplayName("[POST /carpark] Creates a carpark without rates")
    public void check(Vertx vertx, VertxTestContext testContext) {
        JsonObject carparkJson = new JsonObject()
            .put("name", "Test Carpark (without rates)")
            .put("address", "Test Carpark Address");

        post(CARPARK_ENDPOINT)
            .as(BodyCodec.buffer())
            .sendJsonObject(carparkJson, testContext.succeeding(response ->
                testContext.verify(() -> {
                    assertEquals(200, response.statusCode());
                    JsonObject carpark = new JsonObject(response.body());
                    assertNotNull(carpark);
                    assertTrue(carpark.containsKey("id"));
                    assertTrue(carpark.containsKey("name"));
                    assertTrue(carpark.containsKey("address"));
                    assertTrue(carpark.containsKey("ratesCollection"));
                    testContext.completeNow();
                })
            ));
    }

    @Test
    @DisplayName("[POST /carpark] Creates a carpark with rates")
    public void check2(Vertx vertx, VertxTestContext testContext) {
        JsonObject carparkJson = new JsonObject()
            .put("name", "Test Carpark (with rates)")
            .put("address", "Test Carpark Address");

        post(CARPARK_ENDPOINT)
            .as(BodyCodec.buffer())
            .sendJsonObject(carparkJson, testContext.succeeding(response ->
                testContext.verify(() -> {
                    assertEquals(200, response.statusCode());
                    JsonObject carpark = new JsonObject(response.body());
                    assertNotNull(carpark);
                    assertTrue(carpark.containsKey("id"));
                    assertTrue(carpark.containsKey("name"));
                    assertTrue(carpark.containsKey("address"));
                    assertTrue(carpark.containsKey("ratesCollection"));
                    testContext.completeNow();
                })
            ));
    }

    @Test
    @DisplayName("[GET /carpark] Gets all carparks")
    public void checkGetAllCarparks(Vertx vertx, VertxTestContext testContext) {
        get(CARPARK_ENDPOINT)
            .as(BodyCodec.string())
            .send(testContext.succeeding(response ->
                testContext.verify(() -> {
                    assertEquals(200, response.statusCode());
                    assertTrue(response.body().length() > 0);
                    log("GET /carpark > " + response.body());

                    JsonArray carparks = new JsonArray(response.body());
                    log("carparks > " + carparks);
                    assertTrue(carparks.size() > 0);

                    JsonObject firstCarpark = carparks.getJsonObject(0);
                    assertTrue(firstCarpark.containsKey("id"));
                    assertTrue(firstCarpark.containsKey("name"));
                    assertTrue(firstCarpark.containsKey("address"));

                    testContext.completeNow();
                })
            ));
    }

    @Test
    @DisplayName("[GET /carpark/1] Gets one carpark")
    public void checkGetOneCarpark(Vertx vertx, VertxTestContext testContext) {
        get(CARPARK_ENDPOINT + "/1")
            .as(BodyCodec.string())
            .send(testContext.succeeding(response ->
                testContext.verify(() -> {
                    assertEquals(200, response.statusCode());
                    assertTrue(response.body().length() > 0);
                    JsonObject carpark = new JsonObject(response.body());

                    assertNotNull(carpark);
                    assertTrue(carpark.containsKey("id"));
                    assertTrue(carpark.containsKey("name"));
                    assertTrue(carpark.containsKey("address"));
                    assertTrue(carpark.containsKey("ratesCollection"));

                    testContext.completeNow();
                })
            ));
    }

    @Test
    @DisplayName("[DELETE /carpark/1] Deletes one carpark")
    public void checkDeleteOneCarpark(Vertx vertx, VertxTestContext testContext) {
        get(CARPARK_ENDPOINT)
            .as(BodyCodec.string())
            .send(testContext.succeeding(response1 -> {
                JsonArray carparks = new JsonArray(response1.body());
                JsonObject firstCarpark = carparks.getJsonObject(0);
                delete(CARPARK_ENDPOINT + "/" + firstCarpark.getInteger("id"))
                    .as(BodyCodec.string())
                    .send(testContext.succeeding(response2 ->
                        testContext.verify(() -> {
                            assertEquals(200, response2.statusCode());
                            assertTrue(response2.body().length() > 0);
                            JsonObject carpark = new JsonObject(response2.body());

                            assertNotNull(carpark);
                            assertTrue(carpark.containsKey("id"));
                            assertTrue(carpark.containsKey("name"));
                            assertTrue(carpark.containsKey("address"));

                            testContext.completeNow();
                        })
                    ));
                }
            ));
    }

    @AfterAll
    public static void teardown() {
        // Delete all carparks

    }
}
