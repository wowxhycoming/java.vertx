package me.xhy.java.vertx.o1FirstApp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * Created by xuhuaiyu on 2016/11/7.
 */

public class MyFirstVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> fut) {
		vertx.createHttpServer()
				.requestHandler(r -> {
					r.response().end("<h1>Hello from my first Vert.x 3 application</h1>");
				})
				.listen(10001, result -> {
					if (result.succeeded()) {
						fut.complete();
					} else {
						fut.fail(fut.cause());
					}
				});
	}
}