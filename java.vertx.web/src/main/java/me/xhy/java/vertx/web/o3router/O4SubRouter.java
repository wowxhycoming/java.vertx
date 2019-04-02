package me.xhy.java.vertx.web.o3router;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import me.xhy.java.vertx.web.o3router.handler.*;

/**
 * Created by xuhuaiyu on 2016/11/17.
 */

public class O4SubRouter extends AbstractVerticle {

  @Override
  public void start(Future<Void> fut) throws Exception {
    final HttpServer server = vertx.createHttpServer();

    System.out.println("[Deployed]" + Thread.currentThread().getName());

    Router router = Router.router(vertx);
    router.route("/path/*").handler(new Router1Handler());
    router.route("/path/*").handler(new Router2Handler());

    // 构建子路由
    Router subRouter = Router.router(vertx);
    subRouter.route("/sub").handler(new SubRouter1Handler());
    subRouter.route("/sub").handler(new SubRouter2Handler());
    subRouter.route("/sub").handler(new SubRouter3Handler());
    // 路径 /path 下的子路由
    router.mountSubRouter("/path", subRouter);

    router.route("/path/*").handler(new Router3Handler());
    router.route("/path/*").handler(new EndHandler());

    server.requestHandler(router::accept)
        .listen(config().getInteger("http.port", 10024), result -> {
          if (result.succeeded()) {
            fut.complete();
          } else {
            fut.fail(fut.cause());
          }
        });

  }

}
