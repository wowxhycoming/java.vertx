package me.xhy.java.vertx.web.o6deployHandler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Created by xuhuaiyu on 2017/5/25.
 */
public class CompletionHandler implements Handler<AsyncResult<String>> {
    @Override
    public void handle(AsyncResult<String> event) {
        final String result = event.result();

        if(event.succeeded()) {
            System.out.println("[DeployHandler] successful . Result = " + result + " , Thread = " + Thread.currentThread().getName());
        } else {
            System.out.println("[DeployHandler] error occurs . Result = " + result + " , Thread = " + Thread.currentThread().getName());
        }
    }
}
