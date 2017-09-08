package me.xhy.java.vertx.blueprint.s2multi;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.VertxFactory;
import me.xhy.java.vertx.blueprint.s2multi.verticle.TodoVerticle;

/**
 * Created by xuhuaiyu on 2017/6/6.
 */
public class SingleApplicationMain {

    public static void main(String[] args) {

        // java -jar java.vertx.blueprint-1.0-SNAPSHOT-fat.jar (-conf ./config_redis.json)
        final DeploymentOptions redisVertxOptions =
                new DeploymentOptions().setConfig(new JsonObject().put("service.type","redis"));

        final VertxFactory vertxFactory = new VertxFactoryImpl();
        final Vertx vertx = vertxFactory.vertx();

        vertx.deployVerticle(TodoVerticle.class.getName(),redisVertxOptions);

    }
}
