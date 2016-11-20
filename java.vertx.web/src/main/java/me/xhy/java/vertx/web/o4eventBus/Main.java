package me.xhy.java.vertx.web.o4eventBus;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.spi.VertxFactory;
import me.xhy.java.vertx.web.util.Options;

/**
 * Created by xuhuaiyu on 2016/11/19.
 */
public class Main {
	public static void main(String[] args) {

		final VertxOptions vertxOptions = Options.verticleDeploymentOptions("VXWEB.");
		final VertxFactory vertxFactory = new VertxFactoryImpl();
		final Vertx vertx = vertxFactory.vertx(vertxOptions);

		// 发布 standard 类型的 verticle
		vertx.deployVerticle(MessageHandler.class.getName(), Options.verticleDeploymentOptions());

		// 发布 worker 类型的 verticle
		vertx.deployVerticle(UserWorker.class.getName(), Options.workerDeploymentOptions());

	}
}
