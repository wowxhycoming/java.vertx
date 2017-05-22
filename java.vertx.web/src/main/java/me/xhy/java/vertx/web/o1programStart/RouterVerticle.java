package me.xhy.java.vertx.web.o1programStart;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;

/**
 * Created by xuhuaiyu on 2016/11/13.
 */
public class RouterVerticle extends AbstractVerticle {

    @Override
    public void start() {
        // createHttpServer还有一个重载方法，可传入一个io.vertx.core.http.HttpServerOptions实例，
        // 该实例可以进行Web Server级别的配置，也可以理解成为包含Vert.x实例的容器配置项信息，这里就不做说明了。
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
