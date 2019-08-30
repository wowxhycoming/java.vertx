package me.xhy.java.vertx.web.o3router.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by xuhuaiyu on 2016/11/18.
 */
public class OccurErrorHandler implements Handler<RoutingContext> {

  @Override
  public void handle(final RoutingContext routingContext) {
    System.out.println("[OccurErrorHandler]" + Thread.currentThread().getName());
    routingContext.fail(401);
  }
}
