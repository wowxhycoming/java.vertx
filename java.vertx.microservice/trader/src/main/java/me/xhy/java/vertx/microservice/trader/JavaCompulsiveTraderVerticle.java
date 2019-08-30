package me.xhy.java.vertx.microservice.trader;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.MessageSource;
import me.xhy.java.vertx.microservice.common.MicroServiceVerticle;
import me.xhy.java.vertx.microservice.portfolio.model.Portfolio;
import me.xhy.java.vertx.microservice.portfolio.service.PortfolioService;
import me.xhy.java.vertx.microservice.portfolio.service.impl.PortfolioServiceImpl;

/**
 * A compulsive trader...
 */
public class JavaCompulsiveTraderVerticle extends MicroServiceVerticle {

  @Override
  public void start(Future<Void> future) {
    super.start();

    String company = TraderUtils.pickACompany();
    int numberOfShares = TraderUtils.pickANumber();
    System.out.println("Java trader config for company " + company + "and share " + numberOfShares);

    // 异步获取两个服务 market 和 portfolio
    // quote 中将消息发送到 eventBus 上，地址为 "market-data"
    Future<MessageConsumer<JsonObject>> marketFuture = Future.future();
    // portfolio 中异步获取 portfolio 的异步方法
    Future<PortfolioService> portfolioFuture = Future.future();

    // 获取服务，通过handler对Future进行赋值
    MessageSource.getConsumer(discovery,
        new JsonObject().put("name", "market-data"),
        marketFuture.completer());

    EventBusService.getProxy(discovery, PortfolioService.class,
        portfolioFuture.completer());

    CompositeFuture.all(marketFuture, portfolioFuture).setHandler(ar -> {
      if (ar.failed()) {
        future.fail("One of the required service cannot be retrieved" + ar.cause());
      } else {
        MessageConsumer<JsonObject> marketConsumer = marketFuture.result();
        PortfolioService portfolioService = portfolioFuture.result();

        marketConsumer.handler(msg -> {
          JsonObject quote = msg.body();
          System.out.println("trader ========= " + quote);
          TraderUtils.dumbTradingLogic(company, numberOfShares, portfolioService, quote);
        });

        future.complete();
      }
    });

  }

}
