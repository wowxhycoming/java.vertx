package me.xhy.java.vertx.web.o7callbackHell;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.RoutingContext;
import me.xhy.java.vertx.web.o4eventBus.User;

/**
 * Created by xuhuaiyu on 2016/11/19.
 */

public class CallbackHell implements Handler<RoutingContext> {

    @Override
    public void handle(final RoutingContext routingContext) {
        final String name = routingContext.request().getParam("name");
        final String email = routingContext.request().getParam("email");

        // 构造 User
        final User user = new User();
        user.setName(name);
        user.setEmail(email);

        // 获取 EventBus
        final EventBus bus = routingContext.vertx().eventBus();

        /**
         * 使 callback 扁平化
         */

        /**
         * 先创建一个对应类型的 Future （vertx 的 Future）， Future 的泛型为 AsyncResult 的泛型
         */
        Future<Message<Boolean>> future = Future.future();

        /**
         * 为 future 设置 handler ， handler 的类型为 AsyncResult<Message<Boolean>>
         */
        future.setHandler(messageAsyncResult -> {
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

        /**
         * future.completer() 返回 Handler<AsyncResult<T>> 对象
         */
        bus.<Boolean>send("MSG://QUEUE/USER", user.tojson(), future.completer());


    }
}