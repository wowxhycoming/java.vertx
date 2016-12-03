package me.xhy.java.vertx.microservice.quote;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import me.xhy.java.microservice.common.MicroServiceVerticle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by xuhuaiyu on 2016/11/30.
 */
public class GeneratorConfigVerticle extends MicroServiceVerticle {

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
			break;
		}

		vertx.deployVerticle(RestQuoteAPIVerticle.class.getName(), new DeploymentOptions().setConfig(config()));

	}
}
