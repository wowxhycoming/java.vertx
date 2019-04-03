package me.xhy.java.vertx.meet.o5Transaction;

import io.vertx.core.*;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sweet on 2017/6/24.
 */
public class Demo extends AbstractVerticle {

  //  private static final Logger logger = LogManager.getLogger(Demo.class);
  private JDBCClient jdbcClient;

  @Override
  public void start() throws Exception {
    jdbcClient = JDBCClient.createShared(vertx, new JsonObject().put("url", "jdbc:mysql://localhost:3306/vertx?characterEncoding=UTF-8&;useSSL=false").put("driver_class", "com.mysql.jdbc.Driver").put("user", "root").put("password", "xxxx").put("max_pool_size", 30));
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.get("/hello").handler(r -> {
      r.response().end("hello");
    });
    router.get("/db").handler(this::queryDB);
    router.get("/tx").handler(this::tx);
    router.get("/tx2").handler(this::tx2);
    vertx.createHttpServer().requestHandler(router::accept).listen(8081, r -> {
      if (r.succeeded()) {
//        logger.info("服务启动成功,端口 8081");
      } else {
//        logger.error("服务启动失败,error :" + r.cause().getMessage());
      }
    });
  }

  // 下面的代码类似
// try{
// // 开启事务
// ...
// // 提交事务
// } catch (Exception e) {
// // 回滚事务
// }
  private void tx(RoutingContext routingContext) {
    jdbcClient.getConnection(conn -> {
      final SQLConnection connection = conn.result();
      String s1 = "INSERT INTO movie (id, name, duration, director) VALUES ('1432414', 'xxx124', 20, '124xxx')";
      String s2 = "INSERT INTO movie (id, name, duration, director) VALUES ('1432415', 'xxx125', 21, '125xxx')";
      String s3 = "INSERT INTO movie (id, name, duration, director) VALUES ('1432416', 'xxx126', 22, '126xxx')";
      Future.<Void>future(f -> connection.setAutoCommit(false, f)
      ).compose(f1 -> Future.<UpdateResult>future(f -> connection.update(s1, f))
      ).compose(f2 -> Future.<UpdateResult>future(f3 -> connection.update(s2, f3))
      ).compose(f4 -> Future.<UpdateResult>future(f5 -> connection.update(s3, f5))
      ).setHandler(res -> {
        if (res.failed()) {
//          logger.error("事务失败");
          connection.rollback(roll -> {
            if (roll.succeeded()) {
//              logger.info("rollback ok");
              routingContext.response().setStatusCode(500).end("rollback ok");
              connection.close();
            } else {
//              logger.error("rollback error, " + roll.cause().getMessage());
              routingContext.response().setStatusCode(500).end("rollback error");
              connection.close();
            }
          });
        } else {
//          logger.info("事务成功");
          connection.commit(commit -> {
            if (commit.succeeded()) {
//              logger.info("commit ok");
              routingContext.response().end("ok");
              connection.close();
            } else {
//              logger.error("commit error, " + commit.cause().getMessage());
              routingContext.response().end("commit error");
              connection.close();
            }
          });
        }
      });
    });
  }

  private void tx2(RoutingContext routingContext) {
    final String s1 = "INSERT INTO movie (id, name, duration, director) VALUES ('1432414', 'xxx124', 20, '124xxx')";
    final String s2 = "INSERT INTO movie (id, name, duration, director) VALUES ('1432415', 'xxx125', 21, '125xxx')";
    final String s3 = "INSERT INTO movie (id, name_, duration, director) VALUES ('1432416', 'xxx126', 22, '126xxx')";
    jdbcClient.getConnection(conn -> {
      if (conn.succeeded()) {
        final SQLConnection connection = conn.result();
        List<Future> futures = new ArrayList<>();
        Future<Void> future = Future.future(); // 手动开启事务connection.setAutoCommit(false, future);futures.add(future);Future<UpdateResult> future1 = Future.future(); // 执行 Update1connection.update(s1,future1);futures.add(future1);Future<UpdateResult> future2 = Future.future(); // 执行 Update2connection.update(s2,future2);futures.add(future2);Future<UpdateResult> future3 = Future.future(); // 执行 Update3connection.update(s3,future3);futures.add(future3);CompositeFuture.all(futures).setHandler(re -> { if (re.succeeded()){ // 如果都执行成功,提交事务 connection.commit(comm -> { if (comm.succeeded()){ routingContext.response().end("commit ok"); }else { routingContext.response().setStatusCode(500).end(); logger.error(comm.cause().getMessage()); } connection.close(); }); }else { // 如果其中一条执行失败,回滚事务 connection.rollback(roll -> { if (roll.succeeded()){ routingContext.response().setStatusCode(500).end("rollback ok"); logger.info("rollback ok"); }else { routingContext.response().setStatusCode(500).end(); logger.error(roll.cause().getMessage()); } connection.close(); }); }});
      } else {
        routingContext.response().setStatusCode(500).end();
//        logger.error(conn.cause().getMessage());
      }
    });
  }

  private void queryDB(RoutingContext routingContext) {
    jdbcClient.getConnection(conn -> {
      if (conn.succeeded()) {
        final SQLConnection connection = conn.result();
        Future<ResultSet> future = Future.future();
        future.setHandler(res -> {
          if (res.succeeded()) {
            List<JsonObject> list = res.result().getRows();
            if (list != null &&
                list.size() > 0) {
              routingContext.response().end(Json.encodePrettily(list));
              connection.close();
            }
          } else {
//            logger.error(res.cause().getMessage());
            routingContext.response().setStatusCode(500).end();
            connection.close();
          }
        });
        final String sql = "SELECT * FROM movie";
        connection.queryWithParams(sql, null, future);
      } else {
//        logger.error(conn.cause().getMessage());
        routingContext.response().setStatusCode(500).end();
      }
    });

  }

  public static void main(String[] args) {
    Runner.runExample(Demo.class);
  }
}