package me.xhy.java.vertx.web.o3router.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by xuhuaiyu on 2016/11/18.
 */
public class OccurErrHandler implements Handler<RoutingContext> {

	@Override
	public void handle(RoutingContext routingContext) {
		System.out.println("[OccurErrHandler]" + Thread.currentThread().getName());
		routingContext.fail(401);
	}
}
