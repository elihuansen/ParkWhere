package io.parkwhere.integration;

import io.parkwhere.config.Configs;
import io.parkwhere.logging.LogManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public abstract class BaseServiceTest {

    private WebClient webClient;

    @BeforeEach
    void prepare(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(this.getVerticleClass().getCanonicalName(), testContext.succeeding(id -> testContext.completeNow()));
        webClient = WebClient.create(vertx,
            new WebClientOptions()
                .setDefaultPort(Configs.PORT)
                .setDefaultHost("localhost")
        );
        System.out.println("Webclient set => " + webClient);
    }

    public abstract Class<? extends AbstractVerticle> getVerticleClass();

    public HttpRequest<Buffer> get(String requestUri) {
        return webClient.get(requestUri);
    }

    public HttpRequest<Buffer> post(String requestUri) {
        return webClient.post(requestUri);
    }

    public HttpRequest<Buffer> put(String requestUri) {
        return webClient.put(requestUri);
    }

    public HttpRequest<Buffer> delete(String requestUri) {
        return webClient.delete(requestUri);
    }

    public void log(Object obj, Object... objects) {
        log(obj.toString(), objects);
    }

    public void log(String message, Object... objects) {
        LogManager.getLogger(this.getClass()).info(message, objects);
    }
}
