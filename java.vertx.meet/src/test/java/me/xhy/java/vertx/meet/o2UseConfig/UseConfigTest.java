package me.xhy.java.vertx.meet.o2UseConfig;

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
import java.net.ServerSocket;

/**
 * Created by xuhuaiyu on 2016/11/7.
 */
@RunWith(VertxUnitRunner.class)
public class UseConfigTest {

	private Vertx vertx;

	private Integer port;

	@Before
	public void setUp(TestContext context) throws IOException {

		// 增加启动端口配置
		/* 如果8081端口也被占用，随机端口配置
		port = 8081;
		*/

		ServerSocket socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();
		System.out.println(port);

		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));



		vertx = Vertx.vertx();
		vertx.deployVerticle(UseConfig.class.getName(), options,
				context.asyncAssertSuccess());
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
						context.assertTrue(body.toString().contains("Use"));
						async.complete();
					});
				});
	}
}
