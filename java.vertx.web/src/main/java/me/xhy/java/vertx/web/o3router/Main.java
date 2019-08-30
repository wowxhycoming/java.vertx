package me.xhy.java.vertx.web.o3router;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.spi.VertxFactory;
import me.xhy.java.vertx.web.util.Options;

/**
 * Created by xuhuaiyu on 2016/11/13.
 */
public class Main {

  public static void main(final String... args) {

    System.out.println(Runtime.getRuntime().availableProcessors());

    final VertxOptions vertxOptions = Options.getVertxOptions("VXWEB.");
    final VertxFactory factory = new VertxFactoryImpl();
    final Vertx vertx = factory.vertx(vertxOptions);
    final DeploymentOptions verticleOpts = Options.standardDeploymentOptions();

    vertx.deployVerticle(O1Router.class.getName(), verticleOpts);
    vertx.deployVerticle(O2RouterOrder.class.getName(), verticleOpts);
    vertx.deployVerticle(O3BlockingHandler.class.getName(), verticleOpts);
    vertx.deployVerticle(O4SubRouter.class.getName(), verticleOpts);
    vertx.deployVerticle(O5SubRouterOrder.class.getName(), verticleOpts);
    vertx.deployVerticle(O6ReRouter.class.getName(), verticleOpts);
    vertx.deployVerticle(O7FailureHandler.class.getName(), verticleOpts);

  }

}
