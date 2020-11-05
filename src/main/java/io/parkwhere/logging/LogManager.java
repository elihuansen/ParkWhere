package io.parkwhere.logging;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class LogManager {

    static {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        LoggerFactory.initialise();
    }

    public static Logger getLogger(final Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
