package me.xhy.java.vertx.web.o4eventBus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

/**
 * Created by xuhuaiyu on 2016/11/19.
 */
public class UserWorker extends AbstractVerticle {

    @Override
    public void start() {
        // 获取 EventBus
        final EventBus bus = vertx.eventBus();

        // 读取Message
        bus.<User>consumer("MSG://QUEUE/USER", caller -> {
            final User user = caller.body();

            // 业务逻辑代码
            System.out.println("[CALLER] Business Service" + user.tojson());

            // 响应执行
            caller.reply(Boolean.TRUE);
        });
    }
}
