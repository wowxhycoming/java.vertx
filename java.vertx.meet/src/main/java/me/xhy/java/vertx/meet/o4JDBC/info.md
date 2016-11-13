# 使用异步的SQL client

还有几个小时就将迎来2016年了，公众号的各位小编感谢各位这一年的关注，后面将会为大家贡献更多的关于Vertx干货，也会考虑组织线下的活动。
提前祝大家新的一年，身体健康，工作顺利，万事如意！

这篇文章是介绍vert.x博客系列的第5篇。在这篇文章中，我们将会看到怎样在vert.x应用中使用JDBC，以及使用vertx-jdbc-client提供的异步的API。

## 回顾

让我们来回顾一下前面的文章内容：
第一篇文章，描述了怎么用Maven建立一个vert.x应用并且执行单元测试
第二篇文章，描述了这个应用怎么变成可配置的
第三篇文章介绍了vertx-web，并且开发了一个小的集合管理应用。这个应用提供了REST API由前端HTML／JavaScript使用
前一篇文章中，怎么来运行集成测试来确保你的应用的行为和预期一致
在这篇文章中，我们回到代码层面上。当前这个应用使用了一个内存map来存储产品（也就是威士忌）。那么现在是时候来使用一个数据库了。在这里，我们将要使用HSQL，你也可以使用其他任何数据库并提供一个JDBC的驱动。与数据库交互是异步的，通过vertx-jdbc-client来完成。
这篇文章的代码在GitHub上的这个项目的第5个分支[https://github.com/cescoffier/my-vertx-first-app/tree/post-5](https://github.com/cescoffier/my-vertx-first-app/tree/post-5)。
PS：大家可以在这里直接下载这个代码，然后跳到最后运行，可以看结果。然后再看下面的内容。这里的内容是讲解实现的原理。

## 异步的？

vert.x的一个特征就是异步性。使用异步的API，你不需要等结果返回，当结果返回的时候vert.x会主动通知你。仅为了说明这个，让我们来看一个简单的例子。
我们假设有个add方法。一般来说，你会像```int r = add(1, 1)```这样来使用它。这是一个同步的API，因此你必须等到结果的返回。
一个异步版本的API将会是这样：```add(1, 1, r -> { /*do something with the result*/})```。在这个版本中，你传入了一个Handler，当结果计算出来时才被调用。这个方法不返回任何东西，并且像这样来实现：
```
public void add(int a, int b, Handler<Integer> resultHandler) {
    int r = a +b;
    resultHandler.handle(r);
}
```
为了避免混淆概念，异步的API并不是多线程。像我们在add例子里看到的，并没有多线程被涉及。

## JDBC，但是是异步的

现在我们看了一些基本的异步的API，让我们来了解下vertx-jdbc-client。这个组件能够让我们通过JDBC driver与数据库交互。这些交互都是异步的，所以当你像以前这样做的时候：
```
String sql = "select * from product";
ResultSet rs = stmt.executeQuery(sql);
```
你要改成现在这样：
```
String sql = "select * from product";
connection.query(sql, result -> {
    // do something with result
});
```
这个模型更加高效，因为这样避免了等待结果。当结果出来时，vert.x会通知你。

## 添加一些Maven依赖

我们需要做的第一件事就是在pom.xml文件中声明两个新的Maven依赖。
```
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-jdbc-client</artifactId>
    <version>3.3.3</version>
</dependency>

<!-- oracle driver -->
<dependency>
    <groupId>com.oracle</groupId>
    <artifactId>ojdbc6</artifactId>
    <version>11.2.0</version>
</dependency>
```

## 初始化JDBC client

现在我们添加完了依赖了，接下来我们需要创建我们的JDBC client：
在MyFirstVerticle类中，声明一个新变量JDBCClient jdbc，并且在start方法中添加接下来的这句：

```
// final JDBCClient 
jdbc = JDBCClient.createShared(vertx, config(), "My-Whisky-Collection");
```
这句创建了一个JDBC client的实例，并使用这个verticle的配置文件来配置JDBC client。这个配置文件需要提供下面的配置来让JDBC client正常工作：
url : JDBC url，例如：jdbc:hsqldb:mem:db?shutdown=true
driver_class : JDBC的驱动，例如：org.hsqldb.jdbcDriver
我们有了client，接下来我们需要连接数据库。连接数据库是通过使用jdbc.getConnection来实现的，jdbc.getConnection需要传入一个Handler<AsyncResult<SQLConnection>>的参数。

让我们深入的了解下这个类型。首先，这是一个Handler，因此当结果准备好的时候它就会被调用。这个结果是AsyncResult<SQLConnection>的一个实例。
AsyncResult是vert.x提供的一个结构，使用它能够让我们知道连接数据库的操作是成功或失败了。如果成功了，它就会提供一个结果，这里结果是一个SQLConnection的实例。

```
if (ar.failed()) {
    System.out.println(ar.cause().getMessage());
} else {
    result = ar.result();
}
```
再让我们回到我们的SQLConnection。我们需要获取到它，然后开始启动这个rest的应用。这改变了我们怎样启动我们的应用，因为它将会变成异步的。
因此，如果我们将启动序列划分成多个小块，它将会是这样的：

```
startBankend(
    (connection) -> createSomeDate(connection, 
        (nothing) -> startWebApp(
            (http) -> complateStartup(http, fut)
        ), fut
    ), fut);
```

1. startBackend- 获取一个SQLConnection对象，然后调用下一步
2. createSomeData- 初始化数据库并插入一些数据。当完成后，调用下一步
3. startWebApp- 启动我们的web应用
4. completeStartup- 最后完成我们的启动
fut是由vert.x传入的一个未来的结论，让我们报告应用已经启动了或者在启动过程中遇到的问题。
让我们来看一下startBackend方法：

```
private void startBackend(Handler<AsyncResult<SQLConnection>> next, Future<Void> fut) {
    jdbc.getConnection(ar -> {
        if (ar.failed()) {
            fut.fail(ar.cause());
        } else {
            next.handle(Future.succeededFuture(ar.result()));
        }
    });
}
```

这个方法获取了一个SQLConnection对象，检查这个操作是否完成。如果成功了，它会调用下一步。失败了，就会报告一个错误。
其他的方法遵循同样的模式：
1. 检查上一步操作是否成功
2. 处理业务逻辑
3. 调用下一步

## 大量的SQL


```
```
```
```
```
```
