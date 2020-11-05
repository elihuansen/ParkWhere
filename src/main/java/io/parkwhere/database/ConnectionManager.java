package io.parkwhere.database;

import io.parkwhere.config.Configs;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

public class ConnectionManager {

    private Vertx vertx;
    private PgPool connectionPool;

    public ConnectionManager(Vertx vertx) {
        this.vertx = vertx;
        init();
    }

    private void init() {
        PgConnectOptions connectOptions = new PgConnectOptions()
            .setPort(Configs.DB_PORT)
            .setHost(Configs.DB_HOST)
            .setDatabase(Configs.DB_NAME)
            .setUser(Configs.DB_USER)
            .setPassword(Configs.DB_PASS);

        // Pool options
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

        // Create the client pool
        connectionPool = PgPool.pool(vertx, connectOptions, poolOptions);
    }

    public PgPool getPool() {
        return connectionPool;
    }
}
