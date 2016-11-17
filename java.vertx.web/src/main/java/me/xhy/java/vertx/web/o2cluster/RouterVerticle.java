package me.xhy.java.vertx.web.o2cluster;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;

/**
 * Created by xuhuaiyu on 2016/11/13.
 */
public class RouterVerticle extends AbstractVerticle {

	@Override
	public void start() {
		final HttpServer server = vertx.createHttpServer();

		// 同一个类型的Verticle实例可共享端口，如同上边的代码演示的，10个Verticle实例共享了8080的端口。
		// 不同的Verticle实例目前没找到共享端口的方法，暂时定义为不能共享端口。
		// 如果两个不同的Verticle监听了同一个端口会出现JVM_BIND的端口占用异常。
		System.out.println("部署：" + Thread.currentThread().getName());

		server.requestHandler(request -> {
			System.out.println("Handler:" + Thread.currentThread().getName());
			request.response().end("The First Vert.x Web Demo");
		});

		server.listen(10011);
	}
}
