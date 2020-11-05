package io.parkwhere;

import io.parkwhere.config.Configs;
import io.parkwhere.database.ConnectionManager;
import io.parkwhere.logging.LogManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlConnection;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LogManager.getLogger(MainVerticle.class);

    @Override
    public void start() {
        LOGGER.info("Starting MainVerticle...");
        startHttpServer();
        PgPool pool = new ConnectionManager(vertx).getPool();
        pool.getConnection(event -> {
            if (event.succeeded()) {
                SqlConnection conn = event.result();
                LOGGER.info("Connected to database => " + conn);
            } else {
                LOGGER.fatal("Connection to database failed. Aborting.", event.cause());
            }
        });
    }

    private void startHttpServer() {
        int port = Configs.PORT;
        vertx.createHttpServer()
            .requestHandler(req -> req.response().end("Hello Vert.x!"))
            .listen(port);
        LOGGER.info("Listening on {}", port);
    }
}
