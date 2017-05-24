package me.xhy.java.vertx.web.o4eventBus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

/**
 * Created by xuhuaiyu on 2016/11/19.
 */
public class UserWorker extends AbstractVerticle {

    /**
     * Consumer
     *
     * Consumer本身也是一个Vert.x中的Handler，但是它不可以独立发布到Vert.x中，而只能依赖Verticle实例，
     * 而Consumer通常会做很多后台工作，比如文件读写、网络访问、数据库读写等，那么这种情况下需要创建一个新的Verticle来处理，
     * 这个时候就可以使用Worker类型的Verticle实例直接在后台处理各种复杂工作。
     *
     * 注意：决定这个 Verticle 性质是在 deploy 过程。
     */

    @Override
    public void start() {

        System.out.println("[worker]" + Thread.currentThread().getName());

        // 获取 EventBus
        final EventBus bus = vertx.eventBus();

        // 读取Message
        /**
         * consumer 的第一个参数 Addressing。
         *
         * consumer 的第二个参数 Handler<Message<T>>， 这个 Handler 会从地址中得到消息对象 Message 。
         *
         * 最终调用 caller.reply() 方法将处理结果发送出去，而 reply() 方法之后会执行 bus.send() 中的回调函数 。
         */
        bus.<JsonObject>consumer("MSG://QUEUE/USER", caller -> {
            System.out.println("[response] " + Thread.currentThread().getName());
            final JsonObject data = caller.body();

            final User user = new User().toUser(data);

            // 业务逻辑代码
            System.out.println("[CALLER] Business Service" + user.tojson());

            // 响应执行
            caller.reply(Boolean.TRUE);
        });

        // subscribe
        bus.<JsonObject>consumer("MSG://PUBLISH/USER", handler -> {
            System.out.println("[subscribe]" + Thread.currentThread().getName());
            final User user = new User();
            final User user1 = user.toUser(handler.body());
            System.out.println(user1);
        });
    }
}
