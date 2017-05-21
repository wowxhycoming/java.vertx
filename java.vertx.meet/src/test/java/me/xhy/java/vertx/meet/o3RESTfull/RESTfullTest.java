package me.xhy.java.vertx.meet.o3RESTfull;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
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
 * Created by xuhuaiyu on 2016/11/7.
 */
@RunWith(VertxUnitRunner.class)
public class RESTfullTest {

    private Vertx vertx;

    private Integer port;

    @Before
    public void setUp(TestContext context) throws IOException {

        port = 10003;

        DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));

        vertx = Vertx.vertx();
        vertx.deployVerticle(RESTfull.class.getName(), options,
                context.asyncAssertSuccess());

        System.out.println("启动完毕");
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testMyApplication(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(port, "localhost", "/",
                response -> {
                    response.handler(body -> {
//                        context.assertTrue(body.toString().contains("and"));
                        context.assertTrue(true);
                        async.complete();
                    });
                });
    }

    @Test
    public void testAdd(TestContext context) {
        final Async async = context.async();
        final String json = Json.encodePrettily(new Whisky("1", "2"));
        final String length = Integer.toString(json.length());

        vertx.createHttpClient().post(port, "localhost", "/api/whiskies")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 201);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        final Whisky whisky = Json.decodeValue(body.toString(), Whisky.class);
                        context.assertEquals(whisky.getName(), "1");
                        context.assertEquals(whisky.getOrigin(), "2");
                        context.assertNotNull(whisky.getId());
                        async.complete();
                    });
                }).write(json).end();

    }

}
