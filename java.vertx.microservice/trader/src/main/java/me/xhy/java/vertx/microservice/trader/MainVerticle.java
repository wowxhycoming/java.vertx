package me.xhy.java.vertx.microservice.trader;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;

/**
 * The main verticle creating compulsive traders.
 */
public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {

    // Java traders
    vertx.deployVerticle(JavaCompulsiveTraderVerticle.class.getName(), new DeploymentOptions().setInstances(2));

    // Groovy traders...
//    vertx.deployVerticle("GroovyCompulsiveTraderVerticle.groovy");

  }
}
