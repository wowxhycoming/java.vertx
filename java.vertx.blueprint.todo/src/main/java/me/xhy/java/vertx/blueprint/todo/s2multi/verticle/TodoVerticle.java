package me.xhy.java.vertx.blueprint.todo.s2multi.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.redis.RedisOptions;
import me.xhy.java.vertx.blueprint.todo.constant.Constants;
import me.xhy.java.vertx.blueprint.todo.entity.Todo;
import me.xhy.java.vertx.blueprint.todo.s2multi.service.TodoService;
import me.xhy.java.vertx.blueprint.todo.s2multi.service.impl.JdbcTodoService;
import me.xhy.java.vertx.blueprint.todo.s2multi.service.impl.RedisTodoService;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by xuhuaiyu on 2017/6/6.
 */
public class TodoVerticle extends AbstractVerticle {

    private static final String HTTP_HOST = "127.0.0.1";
    private static final int HTTP_PORT = 10032;

    private TodoService service;

    @Override
    public void start(Future<Void> future) throws Exception {
        System.out.println("=========================");
        System.out.println(config());

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

        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods));

        // routes
        router.get(Constants.API_GET).handler(this::handleGetTodo);
        router.get(Constants.API_LIST_ALL).handler(this::handleGetAll);
        router.post(Constants.API_CREATE).handler(this::handleCreateTodo);
        router.patch(Constants.API_UPDATE).handler(this::handleUpdateTodo);
        router.delete(Constants.API_DELETE).handler(this::handleDeleteOne);
        router.delete(Constants.API_DELETE_ALL).handler(this::handleDeleteAll);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(HTTP_PORT, HTTP_HOST, result -> {
                    if (result.succeeded())
                        future.complete();
                    else
                        future.fail(result.cause());
                });

        initData();
    }

    private void initData() {
        System.out.println("action initData");
        final String serviceType = config().getString("service.type", "redis");
        System.out.println("Service Type: " + serviceType);
        switch (serviceType) {
            case "jdbc":
                service = new JdbcTodoService(vertx, config());
                break;
            case "redis":
            default:
                RedisOptions config = new RedisOptions()
                        .setHost(config().getString("redis.host", "127.0.0.1"))
                        .setPort(config().getInteger("redis.port", 6379));
                service = new RedisTodoService(vertx, config);
        }

        service.initData().setHandler(res -> {
            if (res.failed()) {
                System.out.println("Persistence service is not running!");
                res.cause().printStackTrace();
            }
        });
    }

    private <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Consumer<T> consumer) {
        return res -> {
            if (res.succeeded()) {
                consumer.accept(res.result());
            } else {
                serviceUnavailable(context);
            }
        };
    }

    private void handleCreateTodo(RoutingContext context) {
        try {
            final Todo todo = wrapObject(new Todo(context.getBodyAsString()), context);
            final String encoded = Json.encodePrettily(todo);

            service.insert(todo).setHandler(resultHandler(context, res -> {
                if (res) {
                    context.response()
                            .setStatusCode(201)
                            .putHeader("content-type", "application/json")
                            .end(encoded);
                } else {
                    serviceUnavailable(context);
                }
            }));
        } catch (DecodeException e) {
            sendError(400, context.response());
        }
    }

    private void handleGetTodo(RoutingContext context) {
        String todoID = context.request().getParam("todoId");
        if (todoID == null) {
            sendError(400, context.response());
            return;
        }

        service.getCertain(todoID).setHandler(resultHandler(context, res -> {
            if (!res.isPresent())
                notFound(context);
            else {
                final String encoded = Json.encodePrettily(res.get());
                context.response()
                        .putHeader("content-type", "application/json")
                        .end(encoded);
            }
        }));
    }

    private void handleGetAll(RoutingContext context) {
        service.getAll().setHandler(resultHandler(context, res -> {
            if (res == null) {
                serviceUnavailable(context);
            } else {
                final String encoded = Json.encodePrettily(res);
                context.response()
                        .putHeader("content-type", "application/json")
                        .end(encoded);
            }
        }));
    }

    private void handleUpdateTodo(RoutingContext context) {
        try {
            String todoID = context.request().getParam("todoId");
            final Todo newTodo = new Todo(context.getBodyAsString());
            // handle error
            if (todoID == null) {
                sendError(400, context.response());
                return;
            }
            service.update(todoID, newTodo)
                    .setHandler(resultHandler(context, res -> {
                        if (res == null)
                            notFound(context);
                        else {
                            final String encoded = Json.encodePrettily(res);
                            context.response()
                                    .putHeader("content-type", "application/json")
                                    .end(encoded);
                        }
                    }));
        } catch (DecodeException e) {
            badRequest(context);
        }
    }

    private Handler<AsyncResult<Boolean>> deleteResultHandler(RoutingContext context) {
        return res -> {
            if (res.succeeded()) {
                if (res.result()) {
                    context.response().setStatusCode(204).end();
                } else {
                    serviceUnavailable(context);
                }
            } else {
                serviceUnavailable(context);
            }
        };
    }

    private void handleDeleteOne(RoutingContext context) {
        String todoID = context.request().getParam("todoId");
        service.delete(todoID)
                .setHandler(deleteResultHandler(context));
    }

    private void handleDeleteAll(RoutingContext context) {
        service.deleteAll()
                .setHandler(deleteResultHandler(context));
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    private void badRequest(RoutingContext context) {
        context.response().setStatusCode(400).end();
    }

    private void notFound(RoutingContext context) {
        context.response().setStatusCode(404).end();
    }

    private void serviceUnavailable(RoutingContext context) {
        context.response().setStatusCode(503).end();
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
}
