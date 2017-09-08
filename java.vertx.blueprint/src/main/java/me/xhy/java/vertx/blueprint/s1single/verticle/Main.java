package me.xhy.java.vertx.blueprint.s1single.verticle;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.VertxFactory;
import me.xhy.java.vertx.blueprint.s2multi.verticle.TodoVerticle;

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
