package me.xhy.java.vertx.web.o2cluster;

import com.hazelcast.config.Config;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxFactoryImpl;
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

		System.out.println(Runtime.getRuntime().availableProcessors());

		// 读取配置
		final VertxOptions vertxOptions = Options.getVertxOptions("VXWEB.");
		final VertxFactory factory = new VertxFactoryImpl();

		// 创建 cluster
		final ClusterManager clusterManager = new HazelcastClusterManager(new Config());
		vertxOptions.setClusterManager(clusterManager);

		// 使用 factory 创建 cluster
		factory.clusteredVertx(vertxOptions, resultHandler -> {
			// 这里的 resultHandler 类型是 io.vertx.core.Handler<AsyncResult<Vertx>>，
			// 所以 resultHandler.result()直接返回 Vertx引用 不需要强制转换
			if(resultHandler.succeeded()) {
				// 从这里开始流程和单例运行的代码一致了
//				final Vertx vertx = factory.vertx(standardDeploymentOptions);
//				final DeploymentOptions verticleOpts = standardDeploymentOptions();
//				vertx.deployVerticle(RouterVerticle.class.getName(), verticleOpts);
				final Vertx vertx = resultHandler.result();
				final DeploymentOptions verticleOpts = Options.standardDeploymentOptions();
				vertx.deployVerticle(RouterVerticle.class.getName(), verticleOpts);
			}
		});

	}

}
