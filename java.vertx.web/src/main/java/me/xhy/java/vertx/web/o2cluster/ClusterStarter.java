package me.xhy.java.vertx.web.o2cluster;

import com.hazelcast.config.Config;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.VertxFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import me.xhy.java.vertx.web.util.Options;
import org.apache.commons.configuration.Configuration;

/**
 * Created by xuhuaiyu on 2016/11/13.
 */
public class ClusterStarter {

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

    // 使用 factory 创建 cluster
    // vertx 1
    factory.clusteredVertx(vertxOptions.setClusterPort(3001), resultHandler -> {
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
        // verticle 1
        verticleOpts.setConfig(new JsonObject().put("http.port", 10021));
        vertx.deployVerticle(RouterVerticle.class.getName(), verticleOpts);
      }
    });

    /**
     * 由于要测试 Cluster ，但是在同一台机器上分别运行两个 ClusterStarter 。
     * 提示 ：
     *  五月 22, 2017 9:31:57 下午 io.vertx.core.impl.VertxImpl
     *  严重: Failed to start event bus
     *  java.net.BindException: Address already in use: bind
     * 看起来像是 event bus 启动失败了，还需要端口？
     */

  }

}
