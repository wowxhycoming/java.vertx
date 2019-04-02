package me.xhy.java.vertx.web.o4eventBus;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by xuhuaiyu on 2017/5/24.
 */
public class BroadcastingHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext event) {

    System.out.println("[publish]" + Thread.currentThread().getName());

    String name = event.pathParam("name");
    String email = event.pathParam("email");

    final User user = new User();
    user.setName(name);
    user.setEmail(email);

    final EventBus eventBus = event.vertx().eventBus();

    eventBus.publish("MSG://PUBLISH/USER", user.tojson());


  }

}
