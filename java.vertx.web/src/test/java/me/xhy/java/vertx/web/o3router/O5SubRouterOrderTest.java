package me.xhy.java.vertx.web.o3router;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 * Created by xuhuaiyu on 2016/11/17.
 */
@RunWith(VertxUnitRunner.class)
public class O5SubRouterOrderTest {

    private Vertx vertx;

    private Integer port;

    @Before
    public void setUp(TestContext context) throws IOException {

        port = 10016;

        DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));

        vertx = Vertx.vertx();
        vertx.deployVerticle(O5SubRouterOrder.class.getName(), options,
                context.asyncAssertSuccess());

        System.out.println("启动完毕");
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testMyApplication(TestContext context) throws InterruptedException {

        final Async async = context.async();

        vertx.createHttpClient().getNow(port, "localhost", "/path/sub",
                response -> {
                    response.handler(body -> {
                        context.assertTrue(true);
                        async.complete();
                    });
                });

    }

}
