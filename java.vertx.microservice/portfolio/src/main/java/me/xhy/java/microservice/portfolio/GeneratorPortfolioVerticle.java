package me.xhy.java.microservice.portfolio;

import io.vertx.serviceproxy.ProxyHelper;
import me.xhy.java.microservice.common.MicroServiceVerticle;
import me.xhy.java.microservice.portfolio.service.PortfolioService;
import me.xhy.java.microservice.portfolio.service.impl.PortfolioServiceImpl;

import static me.xhy.java.microservice.portfolio.service.PortfolioService.ADDRESS;
import static me.xhy.java.microservice.portfolio.service.PortfolioService.EVENT_ADDRESS;

/**
 * Created by xuhuaiyu on 2016/12/19.
 */
public class GeneratorPortfolioVerticle extends MicroServiceVerticle {

	@Override
	public void start() {
		super.start();

		// Create the service object
		// 创建服务实例
		PortfolioServiceImpl service = new PortfolioServiceImpl(vertx, discovery, config().getDouble("money", 10000.00));

		// Register the service proxy on the event bus
		// 借助 ProxyHelper 类，在Event Bus上注册服务
		ProxyHelper.registerService(PortfolioService.class, vertx, service, ADDRESS);

		// Publish it in the discovery infrastructure
		// 将服务发布至服务发现层(discovery infrastructure)，使得该服务能够被发现
		publishEventBusService("portfolio", ADDRESS, PortfolioService.class, ar -> {
			if (ar.failed()) {
				ar.cause().printStackTrace();
			} else {
				System.out.println("Portfolio service published : " + ar.succeeded());
			}
		});

		publishMessageSource("portfolio-events", EVENT_ADDRESS, ar -> {
			if(ar.failed()) {
				ar.cause().printStackTrace();
			} else {
				System.out.println("Portfolio Events Service publish : " + ar.succeeded());
			}
		});

	}
}
