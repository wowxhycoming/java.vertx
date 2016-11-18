package me.xhy.java.vertx.web.o3router;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import me.xhy.java.vertx.web.o3router.handler.*;

/**
 * Created by xuhuaiyu on 2016/11/17.
 */

public class O5SubRouterOrder extends AbstractVerticle {

	@Override
	public void start(Future<Void> fut) throws Exception {
		final HttpServer server = vertx.createHttpServer();

		System.out.println("[Deployed]" + Thread.currentThread().getName());

		Router router = Router.router(vertx);
		router.route("/path/*").order(3).handler(new Router1Handler());
		router.route("/path/sub").order(1).handler(new Router2Handler());

		Router subRouter1 =  Router.router(vertx);
		subRouter1.route("/sub").order(1).handler(new SubRouter1Handler("A"));
		subRouter1.route("/sub").order(2).handler(new SubRouter2Handler("A"));
		subRouter1.route("/sub").order(3).handler(new SubRouter3Handler("A"));
		router.mountSubRouter("/path", subRouter1);

		router.route("/path/*").order(0).handler(new Router3Handler());

		Router subRouter2 = Router.router(vertx);
		subRouter2.route("/sub").order(3).handler(new SubRouter1Handler("B"));
		subRouter2.route("/sub").order(2).handler(new SubRouter2Handler("B"));
		subRouter2.route("/sub").order(1).handler(new SubRouter3Handler("B"));
		router.mountSubRouter("/path", subRouter2);

		router.route("/path/sub").order(-1).handler(new Router4Handler());

		router.route("/path/*").order(-2).handler(new Router5Handler());

		router.route("/path/*").last().handler(new EndHandler());

		server.requestHandler(router::accept)
				.listen(config().getInteger("http.port",10016), result -> {
					if (result.succeeded()) {
						fut.complete();
					} else {
						fut.fail(fut.cause());
					}
				});

	}

}
