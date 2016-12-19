package me.xhy.java.vertx.microservice.trader;

import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.groovy.servicediscovery.types.MessageSource;
import me.xhy.java.microservice.common.MicroServiceVerticle;
import me.xhy.java.microservice.portfolio.model.Portfolio;

/**
 * A compulsive trader...
 */
public class JavaCompulsiveTraderVerticle extends MicroServiceVerticle {

	@Override
	public void start(Future<Void> future) {
		super.start();

		String company = TraderUtils.pickACompany();
		int nunberOfShares = TraderUtils.pickANumber();
		System.out.println("Java trader config for company " + company + "and share " + nunberOfShares);

		// 异步获取两个服务 market 和 portfolio
		Future<MessageConsumer<JsonObject>> marketFuture = Future.future();
		Future<Portfolio> portfolioFuture = Future.future();

		// 获取服务，通过handler对Future进行赋值

	}

}
