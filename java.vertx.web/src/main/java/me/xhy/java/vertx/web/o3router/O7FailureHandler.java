package me.xhy.java.vertx.web.o3router;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import me.xhy.java.vertx.web.o3router.handler.EndHandler;
import me.xhy.java.vertx.web.o3router.handler.ErrorHandler;
import me.xhy.java.vertx.web.o3router.handler.OccurErrorHandler;
import me.xhy.java.vertx.web.o3router.handler.Router1Handler;

/**
 * Created by xuhuaiyu on 2016/11/18.
 */
public class O7FailureHandler extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) throws Exception {

        final HttpServer server = vertx.createHttpServer();

        System.out.println("[Deploy]" + Thread.currentThread().getName());

        Router router = Router.router(vertx);
        router.route("/path/a").order(-1).handler(new OccurErrorHandler()); // 发生错误
        router.route("/path/a").order(0).handler(new Router1Handler()); // 没有执行
        router.route("/path/a").order(1).failureHandler(new ErrorHandler()); // 处理错误
        router.route("/path").last().handler(new EndHandler());

        server.requestHandler(router::accept)
                .listen(config().getInteger("http.port", 10027), result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(fut.cause());
                    }
                });
    }
}
