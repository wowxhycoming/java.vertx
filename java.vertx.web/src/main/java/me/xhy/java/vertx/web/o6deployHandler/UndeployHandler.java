package me.xhy.java.vertx.web.o6deployHandler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Created by xuhuaiyu on 2017/5/25.
 */
public class UndeployHandler implements Handler<AsyncResult<Void>> {
    @Override
    public void handle(AsyncResult<Void> event) {
        if(event.succeeded()) {
            System.out.println("[Un-DeployHandler] successful . Thread = " + Thread.currentThread().getName());
        } else {
            System.out.println("[Un-DeployHandler] error occurs . Thread = " + Thread.currentThread().getName());
        }
    }
}
