package me.xhy.java.vertx.web.o5EventBusClusterIntegrated;

import com.hazelcast.config.Config;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.VertxFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import me.xhy.java.vertx.web.o4eventBus.RouterVerticle;
import me.xhy.java.vertx.web.o4eventBus.UserWorker;
import me.xhy.java.vertx.web.util.Options;
import org.apache.commons.configuration.Configuration;

/**
 * Created by xuhuaiyu on 2017/05/25.
 */
public class IntegratedClusterStarter {

  private static Configuration LOADER;
  private static String VX_FREFIX = "vertx.";


  /**
   * 借用 me.xhy.java.vertx.web.o2cluster 和 me.xhy.java.vertx.web.o4eventBus
   * 下的包，实验 EventBus 的集群。
   */
  public static void main(final String... args) {

    System.out.println(Runtime.getRuntime().availableProcessors());

    // 读取配置
    final VertxOptions vertxOptions = Options.getVertxOptions("VXWEB.");
    final VertxFactory factory = new VertxFactoryImpl();

    // 创建 cluster
    final ClusterManager clusterManager = new HazelcastClusterManager(new Config());
    vertxOptions.setClusterManager(clusterManager);

    // 使用 factory 创建 cluster
    /**
     * vertx 1
     *
     * 下面发布了一个 http server 和 一个 workerVerticle
     */
    factory.clusteredVertx(vertxOptions.setClusterPort(3051), resultHandler -> {
      if (resultHandler.succeeded()) {
        // 从这里开始流程和单例运行的代码一致了
        final Vertx vertx = resultHandler.result();
        final DeploymentOptions verticleOpts = Options.standardDeploymentOptions();

        // verticle 1
        verticleOpts.setConfig(new JsonObject().put("http.port", 10051));
        vertx.deployVerticle(RouterVerticle.class.getName(), verticleOpts);

        // 发布 worker 类型的 verticle
        vertx.deployVerticle(UserWorker.class.getName(), Options.workerDeploymentOptions());
      }
    });


  }

}
