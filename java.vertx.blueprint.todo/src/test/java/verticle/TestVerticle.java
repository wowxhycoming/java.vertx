package verticle;//package me.xhy.java.vertx.buluprint.verticle;
//
//import io.vertx.core.DeploymentOptions;
//import io.vertx.core.Vertx;
//import io.vertx.core.json.JsonObject;
//import io.vertx.ext.unit.TestContext;
//import io.vertx.ext.unit.junit.VertxUnitRunner;
//import me.xhy.java.vertx.blueprint.verticle.SingleApplicationVerticle;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.io.IOException;
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by xuhuaiyu on 2017/6/5.
// */
//@RunWith(VertxUnitRunner.class)
//public class TestVerticle {
//
//    private final int HTTP_PORT = 10031;
//    private Vertx vertx;
//
//    public static final String API_GET = "/todos/:todoId";
//    public static final String API_LIST_ALL = "/todos";
//    public static final String API_CREATE = "/todos";
//    public static final String API_UPDATE = "/todos/:todoId";
//    public static final String API_DELETE = "/todos/:todoId";
//    public static final String API_DELETE_ALL = "/todos";
//
//    @Before
//    public void before(TestContext context) throws IOException {
//
//        // 初始化 vertx 系统
//        vertx = Vertx.vertx();
//        // 部署 verticle 应用
//        vertx.deployVerticle(SingleApplicationVerticle.class.getName(),
//                new DeploymentOptions().setConfig(new JsonObject().put("http.port",HTTP_PORT)));
//
//        context.assertTrue(true);
//        System.out.println("启动完毕");
//    }
//
//    @Test
//    public void startUp(TestContext context) {
//        System.out.println("无自动化测试，通过网页测试 \r\n");
//        System.out.println("获取一个\r\nhttp://127.0.0.1:" + HTTP_PORT + API_GET);
//        System.out.println("获取全部\r\nhttp://127.0.0.1:" + HTTP_PORT + API_LIST_ALL);
//        System.out.println("创建一个\r\nhttp://127.0.0.1:" + HTTP_PORT + API_CREATE);
//        System.out.println("更新一个\r\nhttp://127.0.0.1:" + HTTP_PORT + API_UPDATE);
//        System.out.println("删除一个\r\nhttp://127.0.0.1:" + HTTP_PORT + API_DELETE);
//        System.out.println("删除全部\r\nhttp://127.0.0.1:" + HTTP_PORT + API_DELETE_ALL);
//
//
//        try {
//            TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//}
