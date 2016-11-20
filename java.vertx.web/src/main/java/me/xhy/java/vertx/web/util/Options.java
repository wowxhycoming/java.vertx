package me.xhy.java.vertx.web.util;

import com.sun.istack.internal.NotNull;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Created by xuhuaiyu on 2016/11/20.
 */
public class Options {

	private static Configuration LOADER;
	private static String VX_FREFIX = "vertx.";

	public static void loadProperties() throws ConfigurationException {
		Configuration config = new PropertiesConfiguration("vertx.properties");
		LOADER = config;
	}

	@NotNull
	public static VertxOptions verticleDeploymentOptions(@NotNull String name) {
		final String VX_PREFIX_AND_NAME = VX_FREFIX + name;
		final VertxOptions opts = new VertxOptions();

		try {
			loadProperties();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		// Pool Size Configuration
		// setEventLoopPoolSize 设置Vert.x实例使用的Event Loop线程的数量，默认值为：2 * Runtime.getRuntime().availableProcessors()（可用的处理器个数）
		opts.setEventLoopPoolSize(LOADER.getInt(VX_PREFIX_AND_NAME + "pool.size.event.loop"));
		// setWorkerPoolSize 设置Vert.x实例中支持的Worker线程的最大数量，默认值为20
		opts.setWorkerPoolSize(LOADER.getInt(VX_PREFIX_AND_NAME + "pool.size.worker"));
		// setInternalBlockingPoolSize 设置内部阻塞线程池最大线程数，这个参数主要被Vert.x的一些内部操作使用，默认值为20
		opts.setInternalBlockingPoolSize(LOADER.getInt(VX_PREFIX_AND_NAME + "pool.size.internal.blocking"));

		// cluster configuration
		// setClustered 是否开启Vert.x的Cluster集群模式，默认值为false
		opts.setClustered(LOADER.getBoolean(VX_PREFIX_AND_NAME + "cluster.enabled"));
		// setClusterHost 【Cluster集群模式有效】设置集群运行的默认hostname，默认值为localhost
		opts.setClusterHost(LOADER.getString(VX_PREFIX_AND_NAME + "cluster.host"));
		// setClusterPort 【Cluster集群模式有效】设置集群运行的端口号，可自定义固定端口号，默认值为0（随机分配）
		opts.setClusterPort(LOADER.getInt(VX_PREFIX_AND_NAME + "cluster.port"));

		// ping time out configuration
		// setClusterPingInterval【Cluster集群模式有效】使用ping命令检测Cluster的时间间隔，默认20000，单位毫秒ms，即20秒；
		opts.setClusterPingInterval(LOADER.getLong(VX_PREFIX_AND_NAME + "cluster.ping.interval"));
		// setClusterPingReplyInterval【Cluster集群模式有效】集群响应ping命令的时间间隔，默认20000，单位毫秒ms，即20秒；
		opts.setClusterPingReplyInterval(LOADER.getLong(VX_PREFIX_AND_NAME + "cluster.ping.interval.reply"));

		// blocked thread check interval
		// setBlockedThreadCheckInterval 阻塞线程检查的时间间隔，默认1000，单位ms，即1秒；
		opts.setBlockedThreadCheckInterval(LOADER.getLong(VX_PREFIX_AND_NAME + "blocked.thread.check.interval"));
		// setMaxEventLoopExecuteTime Event Loop的最大执行时间，默认2l * 1000 * 1000000，单位ns，即2秒；
		opts.setMaxEventLoopExecuteTime(LOADER.getLong(VX_PREFIX_AND_NAME + "execute.time.max.event.loop"));
		// setMaxWorkerExecuteTime Worker线程的最大执行时间，默认60l * 1000 * 1000000，单位ns，即60秒；
		opts.setMaxWorkerExecuteTime(LOADER.getLong(VX_PREFIX_AND_NAME + "execute.time.max.worker"));

		// ha configuration
		// setHAEnabled 是否支持HA架构，默认值false；
		/* 如果Vert.x启用了HA模式，其中一个Vert.x实例运行出现了异常或者死掉，
			那么这个Vert.x实例中运行的Verticle会执行重发布Redeploy的过程将运行在该实例中的Verticle实例重新发布到其他正常运行的Vert.x实例中【Verticle Fail-Over】。 */
		opts.setHAEnabled(LOADER.getBoolean(VX_PREFIX_AND_NAME + "ha.enabled"));
		// setHAGroup 【HA模式有效】支持了HA模式过后，可根据不同的组名将Vert.x进行逻辑分组，此方法设置当前Vert.x的逻辑组名，默认__DEFAULT__；
		opts.setHAGroup(LOADER.getString(VX_PREFIX_AND_NAME + "ha.group"));
		// setQuorumSize 【HA模式有效】支持了HA模式过后，此方法设置冲裁节点的数量，默认值1；
		/* 这个值一旦设置过后，如果要发布Verticle组件，则需要运行环境中的节点数量达到这个值才可执行发布，
			比如quorum的值设置成3，如果仅仅运行了两个Vert.x实例，那么这种情况下Verticle不会被发布，必须要运行至少三个Vert.x实例，才会执行发布Deploy流程，Undeploy流程类似 */
		opts.setQuorumSize(LOADER.getInt(VX_PREFIX_AND_NAME + "quorum.size"));
		// setWarningExceptionTime 如果线程阻塞时间超过了这个阀值，那么就会打印警告的堆栈信息，默认为5l * 1000 * 1000000，单位ns，即5秒；
		opts.setWarningExceptionTime(LOADER.getLong(VX_PREFIX_AND_NAME + "warning.exception.time"));

		return opts;
	}

	public static DeploymentOptions verticleDeploymentOptions() {

		final DeploymentOptions opts = new DeploymentOptions();

		opts.setHa(true); // 默认是false
		opts.setInstances(10); // 默认是1
		opts.setWorker(false); // 默认是false，仅为演示
		opts.setMultiThreaded(false); // 默认是false，仅为演示

		/*
		因为RouterVerticle是一个Standard的类型，这里提供常用的关于Verticle的选项设置，其余的配置项可参考DeploymentOptions类的源代码查看，从Vert.x 3.0开始，Verticle类型从2.0的两种（Standard、Worker）升级成了三种：

		Standard Verticles
		Worker Verticles
		Multi-threaded Worker Verticles（并行应用，超过一个线程执行该应用）
		*/

		return opts;
	}

	public static DeploymentOptions workerDeploymentOptions() {

		final DeploymentOptions opts = new DeploymentOptions();

		opts.setHa(true);
		opts.setInstances(100);
		opts.setWorker(true);
		opts.setMultiThreaded(false); // 默认是false，仅为演示

		return opts;
	}

}
