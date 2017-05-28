package me.xhy.java.vertx.web.o4eventBus;

import io.netty.util.Recycler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import me.xhy.java.vertx.web.o6handlers.MessageAsyncResult;

/**
 * Created by xuhuaiyu on 2016/11/19.
 */

public class MessageHandler implements Handler<RoutingContext> {

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
         * 发送消息的方法是 send ，而不是 publish ， 也就是说使用了 EventBus 的 Request/Response 模式 。
         *
         * send 的第一个参数 Addressing ，这个地址在调用publish/send这种方法调用的时候就直接在Event Bus中生成了，
         *  那么对应的Consumer就可以调用consumer方法从这个地址去接收信息。
         *
         * send 的第二个参数 Message ，
         *  Vert.x的Event Bus支持下边几种类型的直接传递 ：Primitive/Simple Type 、 String 、 Buffer 、 JSON（JsonArray/JsonObject）
         *
         * send 方法第三个参数是一个ReplyHandler（类型：io.vertx.core.Handler<AsyncResult<Message<Boolean>>>），它会等待Consumer的处理结果。
         */
        bus.<Boolean>send("MSG://QUEUE/USER",
                user.tojson(),
                messageAsyncResult -> {
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
                }
                // 使用类的方式管理 handler
//                MessageAsyncResult.create(routingContext.response())
                );
    }
}