package me.xhy.java.vertx.meet.o3RESTfull;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import me.xhy.java.vertx.meet.o3RESTfull.Whisky;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by xuhuaiyu on 2016/11/7.
 */

public class RESTfull extends AbstractVerticle {

    // 鸡尾酒容器
    private Map<Integer, Whisky> products = new LinkedHashMap<>();

    // 初始化数据，并没有使用数据库
    private void createSomeData() {
        Whisky bowmore = new Whisky("Bowmore 15 Years Laimrig", "Scotland, Islay");
        products.put(bowmore.getId(), bowmore);
        Whisky talisker = new Whisky("Talisker 57° North", "Scotland, Island");
        products.put(talisker.getId(), talisker);
    }

    @Override
    public void start(Future<Void> fut) {

        this.createSomeData();

        // 创建router对象
        Router router = Router.router(vertx);

        // 绑定 "/" 到router
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html")
                    .end("<h1>Router and RESTfull</h1>");
        });

        // 访问静态文件
        router.route("/assets/*").handler(StaticHandler.create("assets"));

        // 查询
        router.get("/api/whiskies").handler(this::getAll);

        // 允许 /api/whiskies 下的所有router读取请求的body，通过使用router.route().handler(BodyHandler.create())，能让它在全局生效。
        router.route("/api/whiskies*").handler(BodyHandler.create());
//        router.route().handler(BodyHandler.create());
        // 对 /api/whiskies 的 POST 请求映射到addOne方法
        router.post("/api/whiskies").handler(this::addOne);

        router.delete("/api/whiskies/:id").handler(this::deleteOne);
        router.get("/api/whiskies/:id").handler(this::getOne);
        router.put("/api/whiskies/:id").handler(this::updateOne);
        router.delete("/api/whiskies/:id").handler(this::deleteOne);

        vertx.createHttpServer()
                // 将http请求交给router处理
                .requestHandler(router::accept)
                .listen(config().getInteger("http.port", 10003), result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(fut.cause());
                    }
                });
    }

    private void getAll(RoutingContext routingContext) {
        System.out.println("entry getAll");
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(products.values()));
    }

    private void addOne(RoutingContext routingContext) {
        System.out.println("entry addOne");
        // 将 body 转换成 Whisky 对象
        final Whisky whisky = Json.decodeValue(routingContext.getBodyAsString(), Whisky.class);
        products.put(whisky.getId(), whisky);
        routingContext.response().setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(whisky));
    }

    private void deleteOne(RoutingContext routingContext) {
        System.out.println("entry deleteOne");

        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            Integer idAsInteger = Integer.valueOf(id);
            products.remove(idAsInteger);
        }

        routingContext.response().setStatusCode(204).end();
    }

    private void getOne(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Whisky whisky = products.get(idAsInteger);
            if (whisky == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(whisky));
            }
        }
    }

    private void updateOne(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        JsonObject json = routingContext.getBodyAsJson();
        if (id == null || json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Whisky whisky = products.get(idAsInteger);
            if (whisky == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                whisky.setName(json.getString("name"));
                whisky.setOrigin(json.getString("origin"));
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(whisky));
            }
        }
    }

}