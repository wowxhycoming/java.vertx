package me.xhy.java.vertx.web.o3router.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by xuhuaiyu on 2016/11/17.
 */
public class SubRouter2Handler implements Handler<RoutingContext> {

  String name = "";

  public SubRouter2Handler() {
  }

  public SubRouter2Handler(String name) {
    this.name = "-" + name + "-";
  }

  @Override
  public void handle(final RoutingContext event) {
    System.out.println("[SubRouter 2]" + name + Thread.currentThread().getName());
    event.next();
  }
}
