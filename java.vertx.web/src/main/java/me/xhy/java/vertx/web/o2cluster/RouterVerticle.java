package me.xhy.java.vertx.web.o2cluster;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;

/**
 * Created by xuhuaiyu on 2016/11/13.
 */
public class RouterVerticle extends AbstractVerticle {

    @Override
    public void start() {
        final HttpServer server = vertx.createHttpServer();

        System.out.println("部署：" + Thread.currentThread().getName());

        server.requestHandler(request -> {
            System.out.println("Handler:" + Thread.currentThread().getName());
            request.response().end("The Cluster Vert.x Web Demo");
        });

        server.listen(config().getInteger("http.port"));
    }
}
