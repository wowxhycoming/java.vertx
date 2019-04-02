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
import me.xhy.java.vertx.web.util.Options;
import org.apache.commons.configuration.Configuration;

/**
 * Created by xuhuaiyu on 2017/05/25.
 */
public class IntegratedClusterStarter2 {

  private static Configuration LOADER;
  private static String VX_FREFIX = "vertx.";


  public static void main(final String... args) {

    /**
     * cluster 端口可能冲突
     * web 发布端口可能冲突
     * 这里的集群不是 web 集群， 而是集群了 hazelcast ， 类似 ehcache 的一个应用级别的缓存。
     */

    System.out.println(Runtime.getRuntime().availableProcessors());

    // 读取配置
    final VertxOptions vertxOptions = Options.getVertxOptions("VXWEB.");
    final VertxFactory factory = new VertxFactoryImpl();

    // 创建 cluster
    final ClusterManager clusterManager = new HazelcastClusterManager(new Config());
    vertxOptions.setClusterManager(clusterManager);

    /**
     * vertx 2
     *
     * 下面发布了一个 http server ， 这里没有部署 workerVerticle
     *
     * 访问边部署的 http server ， 让其对 EventBus 发起事件， 如果另外一边的 work 可以接收到， 证明 EventBus 集群成功 。
     */
    factory.clusteredVertx(vertxOptions.setClusterPort(3052), resultHandler -> {
      /**
       * 这里的 resultHandler 类型是 io.vertx.core.Handler<AsyncResult<Vertx>>，
       * 所以 resultHandler.result() 直接返回 Vertx 引用，不需要强制转换
       */
      if (resultHandler.succeeded()) {
        // 从这里开始流程和单例运行的代码一致了
        final Vertx vertx = resultHandler.result();
        final DeploymentOptions verticleOpts = Options.standardDeploymentOptions();

        /**
         * 集群嘛，至少启动两个，这里分别指定两个 verticle 的 httpserver 端口
         */
        // verticle 2
        verticleOpts.setConfig(new JsonObject().put("http.port", 10052));
        vertx.deployVerticle(RouterVerticle.class.getName(), verticleOpts);
      }
    });

  }

}
