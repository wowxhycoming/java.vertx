package me.xhy.java.vertx.microservice.quote;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.Random;

/**
 * A verticle simulating the evaluation of a company evaluation in a very unrealistic and irrational way.
 * It emits the new data on the `market` address on the event bus.
 */
public class MarketDataVerticle extends AbstractVerticle {

  String name; // 公司名称
  int variation;
  long period;
  String symbol; // 公司简称
  int stocks;
  double price;

  double bid; // 由买方报出表示愿意按此水平买入的一个价格(买入价)
  double ask; // 买方购买股票时的价格(卖出价)

  int share; // 可卖数量
  private double value; // 价格。open 表示开盘价

  private final Random random = new Random();

  /**
   * Method called when the verticle is deployed.
   */
  @Override
  public void start() {
    // Retrieve the configuration, and initialize the verticle.
    JsonObject config = config();
    System.out.println("config ========== " + config);
    init(config);

    // Every `period` ms, the given Handler is called.
    System.out.println("period ================== " + period);
    vertx.setPeriodic(period, l -> {
      compute();
      send();
    });
  }

  /**
   * Read the configuration and set the initial values.
   *
   * @param config the configuration
   */
  void init(JsonObject config) {
    period = config.getLong("period", 3000L);
    variation = config.getInteger("variation", 100);
    name = config.getString("name");
    Objects.requireNonNull(name);
    symbol = config.getString("symbol", name);
    stocks = config.getInteger("volume", 10000);
    price = config.getDouble("price", 100.0);

    value = price;
    ask = price + random.nextInt(variation / 2);
    bid = price + random.nextInt(variation / 2);

    share = stocks / 2;
  }

  /**
   * Sends the market data on the event bus.
   */
  private void send() {
    JsonObject jo = toJson();
    vertx.eventBus().publish(QuoteDeploymentVerticle.ADDRESS, jo);
    System.out.println("market data publish|||" + Thread.currentThread().getName() + "|" + jo);
  }

  /**
   * Compute the new evaluation...
   */
  void compute() {

    if (random.nextBoolean()) {
      value = value + random.nextInt(variation);
      ask = value + random.nextInt(variation / 2);
      bid = value + random.nextInt(variation / 2);
    } else {
      value = value - random.nextInt(variation);
      ask = value - random.nextInt(variation / 2);
      bid = value - random.nextInt(variation / 2);
    }

    if (value <= 0) {
      value = 1.0;
    }
    if (ask <= 0) {
      ask = 1.0;
    }
    if (bid <= 0) {
      bid = 1.0;
    }

    if (random.nextBoolean()) {
      // Adjust share
      int shareVariation = random.nextInt(100);
      if (shareVariation > 0 && share + shareVariation < stocks) {
        share += shareVariation;
      } else if (shareVariation < 0 && share + shareVariation > 0) {
        share += shareVariation;
      }
    }
  }

  /**
   * @return a json representation of the market data (quote). The structure is close to
   */
  private JsonObject toJson() {
    return new JsonObject()
        .put("exchange", "vert.x stock exchange")
        .put("symbol", symbol)
        .put("name", name)
        .put("bid", bid)
        .put("ask", ask)
        .put("volume", stocks)
        .put("open", price)
        .put("shares", share);

  }
}
