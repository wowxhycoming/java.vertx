package me.xhy.java.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;

/**
 * Created by xuhuaiyu on 2016/11/13.
 */
public class RouterVerticle extends AbstractVerticle {

	@Override
	public void start() {
		final HttpServer server = vertx.createHttpServer();

		server.requestHandler(request -> {
			request.response().end("The First Vert.x Web Demo");
		});

		server.listen(10011);
	}
}
