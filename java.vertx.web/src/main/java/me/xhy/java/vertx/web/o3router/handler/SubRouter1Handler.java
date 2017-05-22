package me.xhy.java.vertx.web.o3router.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by xuhuaiyu on 2016/11/17.
 */
public class SubRouter1Handler implements Handler<RoutingContext> {

    String name = "";

    public SubRouter1Handler() {
    }

    public SubRouter1Handler(String name) {
        this.name = "-" + name + "-";
    }

    @Override
    public void handle(final RoutingContext event) {
        System.out.println("[SubRouter 1]" + name + Thread.currentThread().getName());
        event.next();
    }
}
