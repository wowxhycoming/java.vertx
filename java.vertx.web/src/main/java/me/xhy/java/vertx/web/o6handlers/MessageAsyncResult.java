package me.xhy.java.vertx.web.o6handlers;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerResponse;

/**
 * Created by xuhuaiyu on 2017/5/28.
 * <p>
 * o4.MessageAsyncResult 调用 send 的最后一个参数，可以传入该值 MessageAsyncResult.create(...)
 */
public class MessageAsyncResult implements Handler<AsyncResult<Message<Boolean>>> {

  private transient HttpServerResponse response;

  public static MessageAsyncResult create(final HttpServerResponse response) {
    return new MessageAsyncResult(response);
  }

  private MessageAsyncResult(final HttpServerResponse response) {
    this.response = response;
  }

  @Override
  public void handle(AsyncResult<Message<Boolean>> messageAsyncResult) {

    System.out.println("[MESSAGE HANDLER] publish === " + Thread.currentThread().getName());
    if (messageAsyncResult.succeeded()) {
      final boolean ret = messageAsyncResult.result().body();
      if (ret) {
        response.end(String.valueOf(ret));
      } else {
        Future.failedFuture("User Processing Met Issue.");
      }
    } else {
      Future.failedFuture("Handler internal error");
    }

  }
}
