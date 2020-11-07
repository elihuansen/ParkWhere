package io.parkwhere;

import com.zandero.rest.RestBuilder;
import io.parkwhere.config.Configs;
import io.parkwhere.database.ConnectionManager;
import io.parkwhere.logging.LogManager;
import io.parkwhere.services.carpark.CarparkService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlConnection;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LogManager.getLogger(MainVerticle.class);

    @Override
    public void start() {
        LOGGER.info("Starting MainVerticle...");
        PgPool pool = new ConnectionManager(vertx).getPool();
        startHttpServer(pool);
        pool.getConnection(event -> {
            if (event.succeeded()) {
                SqlConnection conn = event.result();
                LOGGER.info("Connected to database => " + conn);
            } else {
                LOGGER.fatal("Connection to database failed. Aborting.", event.cause());
            }
        });
    }

    private void startHttpServer(PgPool pool) {
        Router router = new RestBuilder(vertx)
            .register(new CarparkService(pool))
            .build();

        int port = Configs.PORT;
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(port);
        LOGGER.info("Listening on {}", port);
    }
}
