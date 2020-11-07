package io.parkwhere.logging;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class LogManager {

    static {
        LoggerFactory.initialise();
    }

    public static Logger getLogger(final Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
