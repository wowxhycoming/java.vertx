package me.xhy.java.vertx.web.o3router.handler;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by xuhuaiyu on 2016/11/18.
 */
public class ErrorHandler implements Handler<RoutingContext> {

  @Override
  public void handle(final RoutingContext routingContext) {
    System.out.println("[ERROR]" + Thread.currentThread().getName());
    System.out.println("[ERROR]" + routingContext.statusCode());

    HttpServerResponse response = routingContext.response();
    response.setStatusCode(routingContext.statusCode()).end("Error Occur");
  }
}
