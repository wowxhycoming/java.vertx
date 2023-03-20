package me.xhy.java.vertx.web.o8GracefulShutdown;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import me.xhy.java.vertx.web.o3router.handler.Router1Handler;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xuhuaiyu on 2016/11/13.
 */
public class HttpVerticle extends AbstractVerticle {

  @Override
  public void start() {
    System.out.println("== HttpVerticle start ==" + Thread.currentThread().getName());
    HttpServerOptions serverOptions = new HttpServerOptions();
    serverOptions.setPort(10080);
    final HttpServer server = vertx.createHttpServer(serverOptions);
    Router router = Router.router(vertx);

    router.route("/").handler(new RouterHandler());

    server.requestHandler(router::accept).listen(a -> {
      System.out.println(Thread.currentThread().getName());
    });
  }
}
