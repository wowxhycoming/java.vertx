package me.xhy.java.vertx.web.o8GracefulShutdown;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author xxx
 * @since 2023-03-14 20:29
 */
public class RouterHandler implements Handler<RoutingContext> {

  @Override
  public void handle(final RoutingContext rx) {
    int i = Main.atomicInteger.incrementAndGet();
    System.out.println("Handler1: Date = " + new Date() + ", 访问次数 = " + i + ", " + Thread.currentThread().getName());
    try {
      TimeUnit.SECONDS.sleep(12);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // 每5次访问有一次长时间等待，模拟卸载时，还有请求滞留在http server中
    /*if (i % 5 == 0) {
      System.out.println("Handler2: Date = " + new Date() + ", 访问次数 = " + i + ", 处理 40s" + ", " + Thread.currentThread().getName());
      try {
        TimeUnit.SECONDS.sleep(40);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("Handler3: Date = " + new Date() + ", 访问次数 = " + i + ", 处理 40s 完成"+ ", " + Thread.currentThread().getName());
    }*/
    rx.response().end("The First Vert.x Web Demo " + i);
  }
}
