import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.VertxFactory;
import me.xhy.java.vertx.microservice.trader.TraderDeploymentVerticle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by xuhuaiyu on 2016/12/3.
 */
public class MainVerticle {

  public static void main(String[] args) throws IOException {

    final VertxFactory vertxFactory = new VertxFactoryImpl();
    final Vertx vertx = vertxFactory.vertx();

    vertx.deployVerticle(TraderDeploymentVerticle.class.getName());
  }

}
