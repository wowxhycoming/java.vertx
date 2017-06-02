package me.xhy.java.vertx.web.o3router;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import me.xhy.java.vertx.web.o3router.handler.*;

/**
 * Created by xuhuaiyu on 2017/5/23.
 */
public class O6ReRouter extends AbstractVerticle {

    public void start(Future<Void> fut) {
        final HttpServer server = vertx.createHttpServer();
        System.out.println("[Deployed]" + Thread.currentThread().getName());

        Router router = Router.router(vertx);
        router.route("/path/a").order(-1).handler(new Router1Handler());
        router.route("/path/b").order(0).handler(new Router2Handler());
        router.route("/path/a").order(1).handler(new Router3Handler());

        /**
         * // 一定要让最初访问的 url 可以结束
         */
        router.route("/path/*").last().handler(new EndHandler());

        // Re-Router
        router.route("/path/a").handler(context -> {
            System.out.println("[REROUTING]");
            context.reroute("/path/b");
        });

        server.requestHandler(router::accept)
                .listen(config().getInteger("http.port", 10026), result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(fut.cause());
                    }
                });
    }
}
