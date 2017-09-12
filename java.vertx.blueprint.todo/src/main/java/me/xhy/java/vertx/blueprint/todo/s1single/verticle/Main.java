package me.xhy.java.vertx.blueprint.todo.s1single.verticle;

import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.spi.VertxFactory;

/**
 * Created by xuhuaiyu on 2017/6/6.
 */
public class Main {

    public static void main(String[] args) {

        final VertxFactory vertxFactory = new VertxFactoryImpl();
        final Vertx vertx = vertxFactory.vertx();

        vertx.deployVerticle(SingleApplicationVerticle.class.getName());

    }
}
