package me.xhy.java.vertx.web.o4eventBus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

/**
 * Created by xuhuaiyu on 2016/11/19.
 */
public class RouterVerticle extends AbstractVerticle {

    @Override
    public void start(final Future future) {

        System.out.println("[standard]" + Thread.currentThread().getName());

        final HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        // 用于 Request/Response 模式
        router.route("/message/:name/:email").handler(new MessageHandler());

        // 用于 Publish/Subscribe 模式
        router.route("/publish/:name/:email").handler(new BroadcastingHandler());

        server.requestHandler(router::accept)
                .listen(config().getInteger("http.port", 10031), result -> {
                    if (result.succeeded()) {
                        future.complete();
                    } else {
                        future.fail(result.cause());
                    }
                });
    }
}
