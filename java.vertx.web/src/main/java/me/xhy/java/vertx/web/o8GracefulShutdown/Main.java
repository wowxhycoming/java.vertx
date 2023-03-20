package me.xhy.java.vertx.web.o8GracefulShutdown;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.spi.VertxFactory;
import me.xhy.java.vertx.web.o6handlers.UndeployHandler;
import me.xhy.java.vertx.web.util.Options;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by xuhuaiyu on 2023/03/16.
 *
 * 优雅关机的测试，在 http 运行期间，使用 undeploy 卸载服务，会不会影响已经处理的请求
 *
 * 结论：
 * 在 undeploy 执行前，所有已经被 router 处理的请求，不会影响。
 *
 * 有些请求先于 undeploy 执行的时间发出，但是当时 http server 没有空闲线程，不能让请求进入 router 阶段。
 * 这种请求有些会被处理
 */
public class Main {
  static AtomicInteger atomicInteger = new AtomicInteger(0);

  public static void main(String[] args) {

    final VertxOptions vertxOptions = Options.getVertxOptions("VXWEB.");
    vertxOptions.setClustered(false);
    vertxOptions.setMaxEventLoopExecuteTime(60L * 1_000_000_000);
    vertxOptions.setMaxWorkerExecuteTime(60L * 1_000_000_000);
    final VertxFactory vertxFactory = new VertxFactoryImpl();
    final Vertx vertx = vertxFactory.vertx(vertxOptions);

    AtomicReference<String> deploymentId = new AtomicReference<>("");
    System.out.println(Thread.currentThread().getName());

    // 部署一个 2 线程的 HTTP Server， 10 秒后卸载。 但是 router 的处理时间是 12s ，所以两个请求就会让 http server 满载。
    // 在 undeploy 前请求 3次，在 undeploy 后请求1次。
    vertx.deployVerticle(HttpVerticle.class.getName(), new DeploymentOptions().setInstances(2), res -> {
      if (res.succeeded()) {
        deploymentId.set(res.result());
        System.out.println("Main    : Date = " + new Date() + ", 部署完成 30s 后开始卸载");
        vertx.deploymentIDs().forEach(System.out::println);

        if (null != deploymentId.get()) {

          // 系统停顿 等待HTTP请求进入，30秒后卸载部署
          try {
            TimeUnit.SECONDS.sleep(10);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          System.out.println("Main    : Date = " + new Date() + ", 卸载 提交");

          vertx.undeploy(deploymentId.get(), new UndeployHandler());

          System.out.println("Main    : Date = " + new Date() + ", 卸载 提交完成");
          vertx.deploymentIDs().forEach(System.out::println);
        }
      }
    });

  }
}
