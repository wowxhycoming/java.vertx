package me.xhy.java.vertx.web.o6handlers;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.spi.VertxFactory;
import me.xhy.java.vertx.web.o4eventBus.RouterVerticle;
import me.xhy.java.vertx.web.o4eventBus.UserWorker;
import me.xhy.java.vertx.web.util.Options;

import java.util.concurrent.TimeUnit;

/**
 * Created by xuhuaiyu on 2016/11/19.
 */
public class Main {
  public static void main(String[] args) {

    final VertxOptions vertxOptions = Options.getVertxOptions("VXWEB.");
    final VertxFactory vertxFactory = new VertxFactoryImpl();
    final Vertx vertx = vertxFactory.vertx(vertxOptions);

    // 重置实例数量为 1 ， 参数列表中增加 DeployHandler
    vertx.deployVerticle(RouterVerticle.class.getName(), Options.standardDeploymentOptions().setInstances(1), new CompletionHandler());

    // 重置实例数量为 1 ， 参数列表中增加 DeployHandler
    vertx.deployVerticle(UserWorker.class.getName(), Options.workerDeploymentOptions().setInstances(1), new CompletionHandler());


    vertx.deployVerticle(RouterVerticle.class.getName(), Options.standardDeploymentOptions().setInstances(1), res -> {
      if (res.succeeded()) {
        final String deploymentId = res.result();
        System.out.println("DeploymentId = " + deploymentId);
        if (null != deploymentId) {
          vertx.undeploy(deploymentId, new UndeployHandler());

          try {
            TimeUnit.SECONDS.sleep(1);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          vertx.deploymentIDs().forEach(System.out::println);
        }
      }
    });

  }
}
