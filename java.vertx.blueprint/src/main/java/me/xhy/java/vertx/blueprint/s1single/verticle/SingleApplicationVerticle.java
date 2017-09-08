package me.xhy.java.vertx.blueprint.s1single.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import me.xhy.java.vertx.blueprint.constant.Constants;
import me.xhy.java.vertx.blueprint.entity.Todo;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by xuhuaiyu on 2017/6/2.
 */
public class SingleApplicationVerticle extends AbstractVerticle {

    private static final String HTTP_HOST = "127.0.0.1";
    private static final String REDIS_HOST = "127.0.0.1";
    private static final int HTTP_PORT = 10031;
    private static final int REDIS_PORT = 6379;

    private RedisClient redis;

    @Override
    public void start(Future<Void> future) throws Exception {
        initData();
        // 创建路由
        Router router = Router.router(vertx);
        // CORS support
        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");
        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PATCH);

        // route()方法（无参数）代表此路由匹配所有请求。
        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods));

        // 作用是处理HTTP请求正文并获取其中的数据。
        // 比如，在实现添加待办事项逻辑的时候，我们需要读取请求正文中的JSON数据，这时候我们就可以用BodyHandler。
        router.route().handler(BodyHandler.create());

        router.get(Constants.API_GET).handler(this::handleGetTodo);
        router.get(Constants.API_LIST_ALL).handler(this::handleGetAll);
        router.post(Constants.API_CREATE).handler(this::handleCreateTodo);
        router.patch(Constants.API_UPDATE).handler(this::handleUpdateTodo);
        router.delete(Constants.API_DELETE).handler(this::handleDeleteOne);
        router.delete(Constants.API_DELETE_ALL).handler(this::handleDeleteAll);

        vertx.createHttpServer() // <4>
                .requestHandler(router::accept)
                .listen(config().getInteger("http.port",HTTP_PORT), HTTP_HOST, result -> {
                    if (result.succeeded())
                        future.complete();
                    else
                        future.fail(result.cause());
                });
    }

    private void initData() {
        RedisOptions config = new RedisOptions()
                .setHost(config().getString("redis.host", REDIS_HOST)) // redis host
                .setPort(config().getInteger("redis.port", REDIS_PORT)); // redis port

        this.redis = RedisClient.create(vertx, config); // create redis client

        redis.hset(Constants.REDIS_TODO_KEY, "24", Json.encodePrettily( // test connection
                new Todo(24, "Something to do...", false, 1, "todo/ex")), res -> {
            if (res.failed()) {
                res.cause().printStackTrace();
            }
        });

    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    /**
     * 获取/获取所有待办事项
     * @param context
     */
    private void handleGetTodo(RoutingContext context) {
        // 通过getParam方法获取路径参数todoId
        String todoID = context.request().getParam("todoId");
        if (todoID == null)
            sendError(400, context.response());
        else {
            redis.hget(Constants.REDIS_TODO_KEY, todoID, x -> {
                if (x.succeeded()) {
                    String result = x.result();
                    if (result == null)
                        sendError(404, context.response());
                    else {
                        context.response()
                                .putHeader("content-type", "application/json")
                                .end(result);
                    }
                } else
                    sendError(503, context.response());
            });
        }
    }

    private void handleGetAll(RoutingContext context) {
        System.out.println("in handleGetAll");
        redis.hvals(Constants.REDIS_TODO_KEY, res -> { // (1)
            if (res.succeeded()) {
                String encoded = Json.encodePrettily(res.result().stream() // (2)
                        .peek(System.out::print)
                        .map(x -> new Todo((String) x))
                        .peek(System.out::print)
                        .collect(Collectors.toList()));
                context.response()
                        .putHeader("content-type", "application/json")
                        .end(encoded); // (3)
            } else
                sendError(503, context.response());
        });
    }

    private Todo wrapObject(Todo todo, RoutingContext context) {
        int id = todo.getId();
        if (id > Todo.getIncId()) {
            Todo.setIncIdWith(id);
        } else if (id == 0)
            todo.setIncId();
        todo.setUrl(context.request().absoluteURI() + "/" + todo.getId());
        return todo;
    }

    /**
     * 创建待办事项
     * @param context
     */
    private void handleCreateTodo(RoutingContext context) {
        try {
            final Todo todo = wrapObject(new Todo(context.getBodyAsString()), context);
            final String encoded = Json.encodePrettily(todo);
            redis.hset(Constants.REDIS_TODO_KEY, String.valueOf(todo.getId()),
                    encoded, res -> {
                        if (res.succeeded())
                            context.response()
                                    .setStatusCode(201)
                                    .putHeader("content-type", "application/json")
                                    .end(encoded);
                        else
                            sendError(503, context.response());
                    });
        } catch (DecodeException e) {
            sendError(400, context.response());
        }
    }

    /**
     * 更新待办事项
     * @param context
     */
    private void handleUpdateTodo(RoutingContext context) {
        try {
            String todoID = context.request().getParam("todoId"); // (1)
            final Todo newTodo = new Todo(context.getBodyAsString()); // (2)
            // handle error
            if (todoID == null || newTodo == null) {
                sendError(400, context.response());
                return;
            }

            redis.hget(Constants.REDIS_TODO_KEY, todoID, x -> { // (3)
                if (x.succeeded()) {
                    String result = x.result();
                    if (result == null)
                        sendError(404, context.response()); // (4)
                    else {
                        Todo oldTodo = new Todo(result);
                        String response = Json.encodePrettily(oldTodo.merge(newTodo)); // (5)
                        redis.hset(Constants.REDIS_TODO_KEY, todoID, response, res -> { // (6)
                            if (res.succeeded()) {
                                context.response()
                                        .putHeader("content-type", "application/json")
                                        .end(response); // (7)
                            }
                        });
                    }
                } else
                    sendError(503, context.response());
            });
        } catch (DecodeException e) {
            sendError(400, context.response());
        }
    }

    private void handleDeleteOne(RoutingContext context) {
        String todoID = context.request().getParam("todoId");
        redis.hdel(Constants.REDIS_TODO_KEY, todoID, res -> {
            if (res.succeeded())
                context.response().setStatusCode(204).end();
            else
                sendError(503, context.response());
        });
    }

    private void handleDeleteAll(RoutingContext context) {
        redis.del(Constants.REDIS_TODO_KEY, res -> {
            if (res.succeeded())
                context.response().setStatusCode(204).end();
            else
                sendError(503, context.response());
        });
    }
}
