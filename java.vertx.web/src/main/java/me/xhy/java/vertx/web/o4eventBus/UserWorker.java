package me.xhy.java.vertx.web.o4eventBus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

/**
 * Created by xuhuaiyu on 2016/11/19.
 */
public class UserWorker extends AbstractVerticle {

	@Override
	public void start() {
		// 获取 EventBus
		final EventBus bus = vertx.eventBus();

		// 读取Message
		bus.<JsonObject>consumer("MSG://QUEUE/USER", caller -> {
			System.out.println("consumer === " + Thread.currentThread().getName());
			final JsonObject data = caller.body();

			final User user = new User().toUser(data);

			// 业务逻辑代码
			System.out.println("[CALLER] Business Service" + user.tojson());

			// 响应执行
			caller.reply(Boolean.TRUE);
		});
	}
}
