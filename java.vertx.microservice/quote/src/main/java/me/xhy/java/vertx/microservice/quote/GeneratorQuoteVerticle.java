package me.xhy.java.vertx.microservice.quote;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import me.xhy.java.microservice.common.MicroServiceVerticle;

/**
 * Created by xuhuaiyu on 2016/11/30.
 */
public class GeneratorQuoteVerticle extends MicroServiceVerticle {

    /**
     * The address on which the data are sent.
     */
    public static final String ADDRESS = "market";

    @Override
    public void start() {
        super.start();

        JsonArray quotes = config().getJsonArray("companies");

        for (Object q : quotes) {
            JsonObject company = (JsonObject) q;
            System.out.println(q);
            // Deploy the verticle with a configuration.
            vertx.deployVerticle(MarketDataVerticle.class.getName(), new DeploymentOptions().setConfig(company));
        }

        vertx.deployVerticle(RestQuoteAPIVerticle.class.getName(), new DeploymentOptions().setConfig(config()));

        // Publish the services in the discovery infrastructure.
        publishMessageSource("market-data", ADDRESS, rec -> {
            if (!rec.succeeded()) {
                rec.cause().printStackTrace();
            }
            System.out.println("Market-Data service published : " + rec.succeeded());
        });

        publishHttpEndpoint("quotes", "localhost", config().getInteger("http.port", 8080), ar -> {
            if (ar.failed()) {
                ar.cause().printStackTrace();
            } else {
                System.out.println("Quotes (Rest endpoint) service published : " + ar.succeeded());
            }
        });

    }
}
