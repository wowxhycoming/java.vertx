package me.xhy.java.vertx.web.o4eventBus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

/**
 * Created by xuhuaiyu on 2016/11/19.
 */
public class RouterVerticle extends AbstractVerticle {

	@Override
	public void start(final Future future) {
		final HttpServer server = vertx.createHttpServer();

		Router router = Router.router(vertx);
		router.route("/path/:name/:email").handler(new MessageHandler());

		server.requestHandler(router::accept)
				.listen(config().getInteger("http.port", 10018), result -> {
					if (result.succeeded()) {
						future.complete();
					} else {
						future.fail(result.cause());
					}
				});
	}
}
