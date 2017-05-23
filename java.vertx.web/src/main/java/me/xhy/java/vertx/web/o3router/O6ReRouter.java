package me.xhy.java.vertx.web.o3router;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import me.xhy.java.vertx.web.o3router.handler.*;
import org.codehaus.groovy.runtime.powerassert.SourceText;

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
        router.route("/path/a").last().handler(new EndHandler());
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
