package io.parkwhere;

import io.parkwhere.config.ConfigManager;
import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {
    vertx.createHttpServer()
        .requestHandler(req -> req.response().end("Hello Vert.x!"))
        .listen(ConfigManager.PORT);
  }
}
