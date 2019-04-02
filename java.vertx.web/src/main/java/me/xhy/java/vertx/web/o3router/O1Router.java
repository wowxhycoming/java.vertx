package me.xhy.java.vertx.web.o3router;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import me.xhy.java.vertx.web.o3router.handler.EndHandler;
import me.xhy.java.vertx.web.o3router.handler.Router1Handler;
import me.xhy.java.vertx.web.o3router.handler.Router2Handler;
import me.xhy.java.vertx.web.o3router.handler.Router3Handler;

/**
 * Created by xuhuaiyu on 2016/11/17.
 */

public class O1Router extends AbstractVerticle {

  @Override
  public void start(Future<Void> fut) throws Exception {
    final HttpServer server = vertx.createHttpServer();

    System.out.println("[Deployed]" + Thread.currentThread().getName());

    Router router = Router.router(vertx);
    router.route("/path/*").handler(new Router1Handler());
    router.route("/path/some/*").handler(new Router2Handler());
    router.route("/path/").handler(new Router3Handler());
    router.route("/path/*").handler(new EndHandler());

    server.requestHandler(router::accept)
        .listen(config().getInteger("http.port", 10021), result -> {
          if (result.succeeded()) {
            fut.complete();
          } else {
            fut.fail(fut.cause());
          }
        });

  }

}
