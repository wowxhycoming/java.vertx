package me.xhy.java.vertx.web.o1programStart;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;

/**
 * Created by xuhuaiyu on 2016/11/13.
 */
public class RouterVerticle extends AbstractVerticle {

  @Override
  public void start() {
    /**
     * createHttpServer 还有一个重载方法，可传入一个 io.vertx.core.http.HttpServerOptions 实例，
     * 该实例可以进行 Web Server 级别的配置，也可以理解成为包含 Vert.x 实例的容器配置项信息
     */
    final HttpServer server = vertx.createHttpServer();

    /**
     * 同一个类型的 Verticle 实例可共享端口，如同上边的代码演示的，10 个 Verticle 实例共享了 8080 的端口。
     * 不同的Verticle实例目前没找到共享端口的方法，暂时定义为不能共享端口。
     * 果两个不同的Verticle监听了同一个端口会出现JVM_BIND的端口占用异常。
     *
     * 控制台会打印: "部署：vert.x-eventloop-thread-0" ~ "部署：vert.x-eventloop-thread-9" (配置文件中的 instances 为 10)，
     * 从打印顺序不固定可以看出， deploy 过程是异步的。
     */
    System.out.println("部署：" + Thread.currentThread().getName());

    server.requestHandler(request -> {
      /**
       * 访问成功后，按住 F5 刷新浏览器，会看到不同的线程再处理请求
       */
      System.out.println("Handler:" + Thread.currentThread().getName());
      request.response().end("The First Vert.x Web Demo");
    });

    server.listen(10011);
  }
}
