# Vert.x实现REST应用

这篇文章也属于“Introduction to Vert.x”系列。所以，让我们先来简单回顾一下之前的内容。
在第一篇文章里，我们开发了一个很简单的Vert.x 3应用，并且了解了怎么测试、打包和执行。在上一篇文章中，我们了解了应用怎样才能变成可配置的，并且如何使用随机的端口进行测试。

现在让我们来稍微走远一些，开发一个CURD的应用。这个应用发布一个HTML页面，这个页面通过REST API与后台进行交互。
API的RESTfull化的程度作为一个不易把握的话题不是本篇的主题，我会让你来决定。

换言之，接下来，我们将会看到：

1. Vert.x web - 一个能让你方便地使用Vert.x创建一个Web应用的框架
2. 如何发布静态资源
3. 如何开发一个REST API

## Vert.x Web
你可能会注意到在前面的文章中，仅仅使用Vert.x Core来处理复杂的HTTP应用将会很麻烦。因此，这是Vertx.Web框架诞生的主因。
它使开发一个基于Vert.x的web应用更加简单，并且不用改变Vert.x的编程哲学

为了使用Vert.x Web，你需要更新pom.xml文件，添加下面的依赖：
```
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-web</artifactId>
    <version>3.3.3</version>
</dependency>
```
这是你使用Vert.x Web唯一的前提。


让我们来使用它。记得吗，在前面的文章中，当我们请求 http://localhost:8080 时，会返回一个Hello World的信息。现在，让我们来使用Vert.x Web来完成同样的事情。
打开io.vertx.blog.first.MyFirstVerticle类，修改start方法。

## 发布静态资源

在start方法中增加一下router

```
router.route("/assets/*").handler(StaticHandler.create("assets"));
```

访问 http://ip:port/assets/文件名 访问静态资源，资源已经打到jar中。

## 使用Vert.x Web实现REST API

创建 Whisky 模型

## 获得我们的产品

首先，Whisky 对象一定要有默认的构造方法，以保证JSON在转换成对象的时候不报错
```
public Whisky() {
    this.id = COUNTER.getAndIncrement();
}
```

其次，检查在 router.post("/api/whiskies") 之前是不是有 同路径的  router.router("/api/whiskies") ，如果有，这个router将会处理素有的请求，router改成get。

增加代码
```
// 允许 /api/whiskies 下的所有router读取请求的body，通过使用router.route().handler(BodyHandler.create())，能让它在全局生效。
router.route("/api/whiskies*").handler(BodyHandler.create());
// 对 /api/whiskies 的 POST 请求映射到addOne方法
router.post("/api/whiskies").handler(this::addOne);
```

```
private void addOne(RoutingContext routingContext) {
    // 将 body 转换成 Whisky 对象
    final Whisky whisky = Json.decodeValue(routingContext.getBodyAsString(), Whisky.class);
    products.put(whisky.getId(), whisky);
    routingContext.response().setStatusCode(201)
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(whisky));
}
```

这个方法开始从请求的body中取出Whisky对象。它只是将body读成一个字符串并将它传入到Json.decodeValue方法里。Whisky这个对象一旦创建好，将被添加到后台的map中，并以JSON的格式返回。

状态码 201 ？ 就像你看到的，我们将response的状态码设为201。这意味着CREATED，并且，在REST API中创建一个entity时，这是通常的用法。默认的vert.x web设置一个200的状态码代表OK。

## 删除一个产品

酒总有喝完的一天，喝完了，我们就需要删除它。在start方法里，添加这一行：

```
router.delete("/api/whiskies/:id").handler(this::deleteOne);
```

在URL里，我们定义一个path parameter为 “ :id ”。
所以，当我们在处理一个相匹配的请求的时候，Vert.x提取路径中与这个参数对应的一段，使得我们能够在handler方法中获得它。例如，/api/whiskies/0将id映射为0。

让我们看一下在handler方法中这个参数是怎样被使用的。根据下面的内容创建一个deleteOne方法。

```
private void deleteOne(RoutingContext routingContext) {
    System.out.println("entry deleteOne");

    String id = routingContext.request().getParam("id");
    if(id == null) {
        routingContext.response().setStatusCode(400).end();
    } else {
        Integer idAsInteger = Integer.valueOf(id);
        products.remove(idAsInteger);
    }

    routingContext.response().setStatusCode(204).end();
}
```

状态码 204 就像你看到的，我们设置了状态码为204 - NO CONTENT。回复HTTP delete动作通常都是无内容的。