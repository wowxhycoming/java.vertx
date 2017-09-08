package me.xhy.java.vertx.blueprint.s2multi;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.VertxFactory;
import me.xhy.java.vertx.blueprint.s2multi.verticle.TodoVerticle;

/**
 * Created by xuhuaiyu on 2017/6/6.
 */
public class MultiApplicationMain {

    public static void main(String[] args) {

        // java -jar java.vertx.blueprint-1.0-SNAPSHOT-fat.jar -conf ./config_jdbc_mysql.json
        final DeploymentOptions jdbcVertxOptions =
                new DeploymentOptions().setConfig(new JsonObject().put("service.type", "jdbc")
                        .put("url", "jdbc:mysql://10.6.10.99:3306/db1test?characterEncoding=UTF-8&useSSL=false")
                        .put("driver_class", "com.mysql.cj.jdbc.Driver")
                        .put("user", "xhy")
                        .put("password", "123123")
                        .put("max_pool_size", 30));

        final VertxFactory vertxFactory = new VertxFactoryImpl();
        final Vertx vertx = vertxFactory.vertx();

        vertx.deployVerticle(TodoVerticle.class.getName(),jdbcVertxOptions);

    }
}
