package me.xhy.java.vertx.web.o4eventBus;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by xuhuaiyu on 2016/11/19.
 */

public class MessageHandler implements Handler<RoutingContext> {

	@Override
	public void handle(final RoutingContext routingContext) {
		final String name = routingContext.request().getParam("name");
		final String email = routingContext.request().getParam("email");

		// 构造User
		final User user = new User();
		user.setName(name);
		user.setEmail(email);

		// 获取EventBus
		final EventBus bus = routingContext.vertx().eventBus();
		bus.<Boolean>send("MSG://QUEUE/USER", user.tojson(), messageAsyncResult -> {
			System.out.println("publish === " + Thread.currentThread().getName());
			if (messageAsyncResult.succeeded()) {
				final boolean ret = messageAsyncResult.result().body();
				if (ret) {
					routingContext.response().end(String.valueOf(ret));
				} else {
					Future.failedFuture("User Processing Met Issue.");
				}
			} else {
				Future.failedFuture("Handler internal error");
			}
		});
	}
}
