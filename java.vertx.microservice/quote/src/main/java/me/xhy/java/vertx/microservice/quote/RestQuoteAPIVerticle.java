package me.xhy.java.vertx.microservice.quote;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This verticle exposes a HTTP endpoint to retrieve the current / last values of the maker data (quotes).
 */
public class RestQuoteAPIVerticle extends AbstractVerticle {

	private Map<String, JsonObject> quotes = new HashMap<>();

	@Override
	public void start() throws Exception {
		vertx.eventBus().<JsonObject>consumer(GeneratorConfigVerticle.ADDRESS)
				.handler(message -> {

					final JsonObject data = message.body();
					quotes.put(data.getString("name"), data);
					System.out.println("consumer--" + Thread.currentThread().getName() + "===" + data.toString());

				});

		vertx.createHttpServer()
				.requestHandler(request -> {
					HttpServerResponse response = request.response()
							.putHeader("content-type", "application/json");

					String company = request.getParam("name");
					if (null == company) {
						String content = Json.encodePrettily(quotes);
						response.end(content);
					} else {
						JsonObject quote = quotes.get(company);
						if (null == quote) {
							response.setStatusCode(404).end();
						} else {
							response.end(Json.encodePrettily(quote));
						}
					}

				}).listen(config().getInteger("http.port"), ar -> {
					System.out.println(ar.succeeded());
					if (ar.succeeded()) {
						System.out.println("Server started");
					} else {
						System.out.println("Cannot start the server: " + ar.cause());
					}
				});
	}
}
