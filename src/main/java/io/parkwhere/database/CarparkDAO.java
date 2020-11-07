package io.parkwhere.database;

import io.parkwhere.logging.LogManager;
import io.parkwhere.model.Carpark;
import io.parkwhere.services.carpark.CarparkService;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;

public class CarparkDAO {

    Logger LOGGER = LogManager.getLogger(CarparkService.class);

    private final static String CARPARK_QUERY_BY_ID  = "SELECT id, name, address, created_at, updated_at FROM carpark WHERE id=$1";
    private final static String CARPARK_QUERY_ALL    = "SELECT id, name, address, created_at, updated_at FROM carpark";
    private final static String CARPARK_INSERT_ONE   = "INSERT INTO carpark (name, address) VALUES ($1, $2)";
    private final static String CARPARK_INSERT_MANY  = "INSERT INTO carpark (name, address) VALUES (?)";
    private final static String CARPARK_UPDATE_ONE   = "UPDATE carpark SET (name, address)=($2, $3) WHERE id=$1";
    private final static String CARPARK_DELETE_BY_ID = "DELETE FROM carpark WHERE id=$1 RETURNING id, name, address";


    private SqlConnection conn;

    public CarparkDAO with(SqlConnection conn) {
        this.conn = conn;
        return this;
    }

    public void queryAll(Handler<List<Carpark>> handler) {
        conn.preparedQuery(CARPARK_QUERY_ALL).execute(ar -> {
            if (ar.succeeded()) {
                List<Carpark> allCarparks = new ArrayList<>();
                RowSet<Row> rows = ar.result();
                LOGGER.debug("queryAll Got {} rows", rows.rowCount());
                for (Row row : rows) {
                    allCarparks.add(
                        new Carpark()
                            .setId(row.getInteger("id"))
                            .setName(row.getString("name"))
                            .setAddress(row.getString("address"))
                            .setCreatedAt(row.getLocalDateTime("created_at"))
                            .setUpdatedAt(row.getLocalDateTime("updated_at"))
                    );
                }
                handler.handle(allCarparks);
            }
        });
    }

    public void queryById(int id, Handler<Carpark> handler) {
        conn
            .preparedQuery(CARPARK_QUERY_BY_ID)
            .execute(
                Tuple.of(id),
                ar -> {
                    if (ar.succeeded()) {
                        RowSet<Row> rows = ar.result();
                        LOGGER.debug("querybyId Got {} rows", rows.rowCount());
                        for (Row row : rows) {
                            Carpark carpark = new Carpark()
                                .setId(row.getInteger("id"))
                                .setName(row.getString("name"))
                                .setAddress(row.getString("address"))
                                .setCreatedAt(row.getLocalDateTime("created_at"))
                                .setUpdatedAt(row.getLocalDateTime("updated_at"));
                            handler.handle(carpark);
                            return;
                        }
                        handler.handle(null);
                    }
                }
            );
    }

    public void insertOne(Carpark carpark, Handler<Boolean> handler) {
        conn
            .preparedQuery(CARPARK_INSERT_ONE)
            .execute(
                Tuple.of(carpark.getName(), carpark.getAddress()),
                ar -> {
                    RowSet<Row> rows = ar.result();
                    if (rows.rowCount() == 1) {
                        handler.handle(true);
                    } else {
                        handler.handle(false);
                    }
                }
            );
    }

    public void insertMany(List<Carpark> carparks, Handler<Boolean> handler) {
        List<Tuple> batch = new ArrayList<>();
        for (Carpark carpark : carparks) {
            batch.add(Tuple.of(carpark.getName(), carpark.getAddress()));
        }
        conn
            .preparedQuery(CARPARK_INSERT_MANY)
            .executeBatch(
                batch,
                ar -> {
                    RowSet<Row> rows = ar.result();
                    if (rows.rowCount() == 1) {
                        handler.handle(true);
                    } else {
                        handler.handle(false);
                    }
                }
            );
    }

    public void updateName(Carpark carpark, Handler<Boolean> handler) {
        conn
            .preparedQuery(CARPARK_UPDATE_ONE)
            .execute(
                Tuple.of(carpark.getId(), carpark.getName(), carpark.getAddress()),
                ar -> {
                    RowSet<Row> rows = ar.result();
                    if (rows.rowCount() > 0) {
                        handler.handle(true);
                    } else {
                        handler.handle(false);
                    }
                }
            );
    }

    public void deleteById(int id, Handler<Carpark> handler) {
        conn
            .preparedQuery(CARPARK_DELETE_BY_ID)
            .execute(Tuple.of(id), ar -> {
                RowSet<Row> rows = ar.result();
                LOGGER.info("rows " + ar.succeeded());
                for (Row row : rows) {
                    LOGGER.info("row => " + row);
                    Carpark carpark = new Carpark()
                        .setId(row.getInteger("id"))
                        .setName(row.getString("name"))
                        .setAddress(row.getString("address"));
                    handler.handle(carpark);
                    return;
                }
                handler.handle(null);
            });
    }
}
