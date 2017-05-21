package me.xhy.java.vertx.meet.o2UseConfig;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * Created by xuhuaiyu on 2016/11/7.
 */

public class UseConfig extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        vertx.createHttpServer()
                .requestHandler(r -> {
                    r.response().end("<h1>Use Config</h1>");
                })
                .listen(config().getInteger("http.port", 10002), result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(fut.cause());
                    }
                });
    }

}