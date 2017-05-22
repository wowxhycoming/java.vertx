package me.xhy.java.vertx.web.o3router.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by xuhuaiyu on 2016/11/17.
 */
public class Router5Handler implements Handler<RoutingContext> {

    @Override
    public void handle(final RoutingContext event) {
        System.out.println("[Router 5]" + Thread.currentThread().getName());
        event.next();
    }
}
