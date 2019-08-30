package me.xhy.java.vertx.blueprint.todo.s2multi.service.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import me.xhy.java.vertx.blueprint.todo.entity.Todo;
import me.xhy.java.vertx.blueprint.todo.s2multi.service.TodoService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by xuhuaiyu on 2017/6/6.
 */
public class JdbcTodoService implements TodoService {

  private final Vertx vertx;
  private final JsonObject config;
  private final JDBCClient client;

  private static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS `todo` (\n" +
      "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
      "  `title` varchar(255) DEFAULT NULL,\n" +
      "  `completed` tinyint(1) DEFAULT NULL,\n" +
      "  `order` int(11) DEFAULT NULL,\n" +
      "  `url` varchar(255) DEFAULT NULL,\n" +
      "  PRIMARY KEY (`id`) )";
  private static final String SQL_INSERT = "INSERT INTO `todo` " +
      "(`id`, `title`, `completed`, `order`, `url`) VALUES (?, ?, ?, ?, ?)";
  private static final String SQL_INSERT_AGAIN = SQL_INSERT;
  private static final String SQL_QUERY = "SELECT * FROM todo WHERE id = ?";
  private static final String SQL_QUERY_ALL = "SELECT * FROM todo";
  private static final String SQL_UPDATE = "UPDATE `todo`\n" +
      "SET\n" +
      "`id` = ?,\n" +
      "`title` = ?,\n" +
      "`completed` = ?,\n" +
      "`order` = ?,\n" +
      "`url` = ?\n" +
      "WHERE `id` = ?;";
  private static final String SQL_DELETE = "DELETE FROM `todo` WHERE `id` = ?";
  private static final String SQL_DELETE_ALL = "DELETE FROM `todo`";

  public JdbcTodoService(JsonObject config) {
    this(Vertx.vertx(), config);
  }

  public JdbcTodoService(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    this.config = config;
    this.client = JDBCClient.createShared(vertx, config);
  }

  private Handler<AsyncResult<SQLConnection>> connHandler(Future future, Handler<SQLConnection> handler) {
    return conn -> {
      if (conn.succeeded()) {
        final SQLConnection connection = conn.result();
        handler.handle(connection);
      } else {
        future.fail(conn.cause());
      }
    };
  }

  @Override
  public Future<Boolean> initData() {

    Future<SQLConnection> connFuture = Future.future();
    Future<SQLConnection> createFuture = Future.future();
    Future<SQLConnection> insertFuture = Future.future();
    Future<SQLConnection> updateFuture = Future.future();
    Future<Boolean> result = Future.future();
    SQLConnection connection;

    // 获取 conn 的过程中如果有没有额外的逻辑，这样写更简洁 。 System.out.println("create connection " + conn); 就是额外的逻辑
    // client.getConnection(connFuture); 或 client.getConnection(connFuture.completer());
    client.getConnection(conn -> {
      System.out.println("create connection " + conn);

      if (conn.succeeded()) {
        connFuture.complete(conn.result());
      } else {
        connFuture.fail(conn.cause());
      }
    });

    Handler<SQLConnection> createHandler = conn -> {
      System.out.println("create table " + conn);

      conn.execute(SQL_CREATE, create -> {
        if (create.succeeded()) {
          createFuture.complete(conn);
        } else {
          createFuture.fail(create.cause());
        }
      });
    };

    Handler<SQLConnection> insertHandler = conn -> {
      System.out.println("insert data " + conn);
      Todo todo = new Todo(Math.abs(new java.util.Random().nextInt()),
          "Something to do...", false, 1, "todo/ex");

      conn.updateWithParams(SQL_INSERT, new JsonArray().add(todo.getId())
          .add(todo.getTitle())
          .add(todo.getCompleted())
          .add(todo.getOrder())
          .add(todo.getUrl()), r -> {
        if (r.failed()) {
          insertFuture.fail(r.cause());
        } else {
          insertFuture.complete(conn);
        }
      });
    };

    Handler<SQLConnection> updateHandler = conn -> {
      System.out.println("update data " + conn);
      Todo todo = new Todo(Math.abs(new java.util.Random().nextInt()),
          "Something to do...", false, 1, "todo/ex");

      conn.updateWithParams(SQL_INSERT_AGAIN, new JsonArray().add(todo.getId())
          .add(todo.getTitle())
          .add(todo.getCompleted())
          .add(todo.getOrder())
          .add(todo.getUrl()), r -> {
        if (r.failed()) {
          updateFuture.fail(r.cause());
        } else {
          updateFuture.complete(conn);
        }
      });
    };

        /* compose 流程
         compose 前面的 Future 如果成功了，将被传递给第一个参数，然后把第一个参数的执行结果放到第二个参数中；（handler 可见 第二个参数，直接放入操作结果）
                              如果失败了，将被传递给第二个参数
         compose 永远返回第二个参数
         无论调用 compose 的 Future 是否成功，最终都会执行 compose 内参数的 Handler ，因为都有结果了，所要把逻辑放到 compose 的第一个参数中
         */
    // 如果获取连接成功了，下面 compose 中每一个 SQLConnection 对象的引用地址都是相同的，没有重复创建对象，传递了引用
    // compose 链最后的 setHandler 是一定会执行的，对上面成功申请的资源进行关闭
    // 异常处理 : vertx 不会因为异常用户的中断程序
    connFuture.compose(createHandler, createFuture)
        .compose(insertHandler, insertFuture)
        .compose(updateHandler, updateFuture)
        .setHandler(res -> {
          System.out.println("compose handler");

          // 如果获取连接成功了，在这里关闭连接
          if (connFuture.succeeded()) {
            SQLConnection sqlConnection = connFuture.result();
            System.out.println(sqlConnection);
            sqlConnection.close();
          }

          if (res.succeeded()) {
            result.complete(true);
          } else {
            result.fail(res.cause());
          }
        });

    return result;
  }

  @Override
  public Future<Boolean> insert(Todo todo) {
    Future<Boolean> result = Future.future();
    client.getConnection(connHandler(result, connection -> {
      connection.updateWithParams(SQL_INSERT, new JsonArray().add(todo.getId())
          .add(todo.getTitle())
          .add(todo.getCompleted())
          .add(todo.getOrder())
          .add(todo.getUrl()), r -> {
        if (r.failed()) {
          result.fail(r.cause());
        } else {
          result.complete(true);
        }
        connection.close();
      });
    }));
    return result;
  }

  @Override
  public Future<List<Todo>> getAll() {
    Future<List<Todo>> result = Future.future();
    client.getConnection(connHandler(result, connection ->
        connection.query(SQL_QUERY_ALL, r -> {
          if (r.failed()) {
            result.fail(r.cause());
          } else {
            List<Todo> todos = r.result().getRows().stream()
                .map(Todo::new)
                .collect(Collectors.toList());
            result.complete(todos);
          }
          connection.close();
        })));
    return result;
  }

  @Override
  public Future<Optional<Todo>> getCertain(String todoID) {
    Future<Optional<Todo>> result = Future.future();
    client.getConnection(connHandler(result, connection -> {
      connection.queryWithParams(SQL_QUERY, new JsonArray().add(todoID), r -> {
        if (r.failed()) {
          result.fail(r.cause());
        } else {
          List<JsonObject> list = r.result().getRows();
          if (list == null || list.isEmpty()) {
            result.complete(Optional.empty());
          } else {
            result.complete(Optional.of(new Todo(list.get(0))));
          }
        }
        connection.close();
      });
    }));
    return result;
  }

  @Override
  public Future<Todo> update(String todoId, Todo newTodo) {
    Future<Todo> result = Future.future();
    client.getConnection(connHandler(result, connection -> {
      this.getCertain(todoId).setHandler(r -> {
        if (r.failed()) {
          result.fail(r.cause());
        } else {
          Optional<Todo> oldTodo = r.result();
          if (!oldTodo.isPresent()) {
            result.complete(null);
            return;
          }
          Todo fnTodo = oldTodo.get().merge(newTodo);
          int updateId = oldTodo.get().getId();
          connection.updateWithParams(SQL_UPDATE, new JsonArray().add(updateId)
              .add(fnTodo.getTitle())
              .add(fnTodo.getCompleted())
              .add(fnTodo.getOrder())
              .add(fnTodo.getUrl())
              .add(updateId), x -> {
            if (x.failed()) {
              result.fail(r.cause());
            } else {
              result.complete(fnTodo);
            }
            connection.close();
          });
        }
      });
    }));
    return result;
  }

  private Future<Boolean> deleteProcess(String sql, JsonArray params) {
    Future<Boolean> result = Future.future();
    client.getConnection(connHandler(result, connection ->
        connection.updateWithParams(sql, params, r -> {
          if (r.failed()) {
            result.complete(false);
          } else {
            result.complete(true);
          }
          connection.close();
        })));
    return result;
  }

  @Override
  public Future<Boolean> delete(String todoId) {
    return deleteProcess(SQL_DELETE, new JsonArray().add(todoId));
  }

  @Override
  public Future<Boolean> deleteAll() {
    return deleteProcess(SQL_DELETE_ALL, new JsonArray());
  }
}
