package io.parkwhere.services.carpark;

import com.zandero.rest.annotation.BodyParam;
import io.parkwhere.database.CarparkDAO;
import io.parkwhere.domain.CarparkChargeCalculator;
import io.parkwhere.logging.LogManager;
import io.parkwhere.model.Carpark;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlConnection;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;

@Path("/carpark")
public class CarparkService {

    Logger LOGGER = LogManager.getLogger(CarparkService.class);

    private CarparkChargeCalculator carparkChargeCalculator;
    private CarparkDAO carparkDAO;
    private PgPool pgPool;

    public CarparkService(PgPool pgPool) {
        this.pgPool = pgPool;
        carparkChargeCalculator = new CarparkChargeCalculator();
        carparkDAO = new CarparkDAO();
    }

    @GET
    @Path("/")
    @Produces("application/json")
    public Promise<JsonArray> getAllCarparks() {
        Promise<JsonArray> promise = Promise.promise();
        pgPool.getConnection(event -> {
            if (event.succeeded()) {
                SqlConnection conn = event.result();
                carparkDAO
                    .with(conn)
                    .queryAll(carparks -> {
                        LOGGER.info("GET /carpark => {}", carparks);
                        JsonArray jsonArray = new JsonArray();
                        for (Carpark carpark : carparks) {
                            jsonArray.add(JsonObject.mapFrom(carpark));
                        }
                        promise.complete(jsonArray);
                    });
            } else {
                promise.fail("Failed to connect to database");
            }
        });
        return promise;
    }

    @GET
    @Path("/:id")
    @Produces("application/json")
    public Promise<JsonObject> getCarpark(@PathParam("id") int id) {
        Promise<JsonObject> promise = Promise.promise();
        pgPool.getConnection(event -> {
            if (event.succeeded()) {
                LOGGER.debug("GET /carpark => Querying for carpark {}", id);
                SqlConnection conn = event.result();
                carparkDAO
                    .with(conn)
                    .queryById(id, carpark -> {
                        LOGGER.info(carpark);
                        if (carpark == null) {
                            promise.fail("GET /carpark => No such carpark");
                        } else {
                            promise.complete(JsonObject.mapFrom(carpark));
                        }
                    });
            } else {
                promise.fail("Failed to connect to database");
            }
        });
        return promise;
    }

    @POST
    @Path("/")
    @Consumes("application/json")
    @Produces("application/json")
    public Promise<JsonObject> createCarpark(@BodyParam Carpark carpark) {
        Promise<JsonObject> promise = Promise.promise();
        pgPool.getConnection(event -> {
            if (event.succeeded()) {
                LOGGER.debug("POST /carpark => Creating {}", carpark);
                SqlConnection conn = event.result();
                carparkDAO
                    .with(conn)
                    .insertOne(carpark, success -> {
                        if (success) {
                            LOGGER.info("POST /carpark => Created {}", carpark);
                            promise.complete(JsonObject.mapFrom(carpark));
                        } else {
                            LOGGER.warn("POST /carpark => Failed {}", carpark);
                            promise.fail("No such carpark");
                        }
                    });
            } else {
                promise.fail("Failed to connect to database");
            }
        });
        return promise;
    }

    @PUT
    @Path("/:id")
    @Consumes("application/json")
    @Produces("application/json")
    public Promise<JsonObject> updateCarpark(@PathParam("id") int id) {
        Promise<JsonObject> promise = Promise.promise();
        promise.complete();
        return promise;
    }

    @DELETE
    @Path("/:id")
    @Produces("application/json")
    public Promise<JsonObject> deleteCarpark(@PathParam("id") int id) {
        Promise<JsonObject> promise = Promise.promise();
        pgPool.getConnection(event -> {
            if (event.succeeded()) {
                LOGGER.debug("DELETE /carpark => Deleting Carpark.id={}", id);
                SqlConnection conn = event.result();
                carparkDAO
                    .with(conn)
                    .deleteById(id, carpark -> {
                        if (carpark != null) {
                            LOGGER.info("DELETE /carpark => Carpark.id={}", id);
                            promise.complete(JsonObject.mapFrom(carpark));
                        } else {
                            LOGGER.warn("DELETE /carpark => Failed to delete Carpark.id={}", id);
                            promise.fail(new Exception("No such carpark"));
                        }
                    });
            } else {
                promise.fail("Failed to connect to database");
            }
        });
        return promise;
    }
}
