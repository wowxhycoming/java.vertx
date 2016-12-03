# Vert.x 应用配置

## 温故知新

在上一篇 ”第一个Vert.x3应用程序“ 中，我们开发了一个非常简单的Vert.x3应用程序，还包括应用如何测试，打包，运行。
看起来非常不错，不是吗？但是这仅仅只是个开始，在这篇文章中，我们将增强我们的应用来支持外部配置。

提醒一下，通过上一篇文章，我们有了一个应用，这个应用启动一个监听8080端口的服务器，并且礼貌的回复”hello“给所有的请求，前面的代码在这边也是可用的。

## 我们为什么需要配置？

这是个很好的问题。现在程序运行的很好，但是，如果我们让你将应用部署到一台8080端口已经被占用的机器呢？
那我们就需要为这台机器在应用和测试代码中修改端口。这是个让人很郁闷的事情。幸运的是，Vert.x应用是可以配置的。

Vert.x是用JSON格式来配置的，所以不要以为很复杂。JSON数据可以通过命令行或者是使用API传入到verticle。让我们开始吧。

## 不再使用8080端口

修改的第一步，FirstVerticle类不再绑定8080端口，改为从配置文件中读取。


修改的很简单，将原来的代码中，listen里的第一个参数8080，替换为```config().getInteger("http.port", 8080)```。
这里，我们的代码会请求这个配置并检查http.port属性是否配置了。如果没有，将会默认使用8080端口。配置是从JsonObject中获取的。

由于我们将8080设为默认的端口，你仍然可以打包我们的程序并像之前一样运行。

## API－基本配置－在测试中使用随机端口

现在，这个应用是可配置的，让我们来试着提供一个配置。在我们的测试里，我们将应用的端口配置成为8081。所以，我们使用下面这句代码来部署我们的verticle:
```
vertx.deployVerticle(UseConfit.class.getName(), context.asyncAssertSuccess());
```

现在让我们来传入一些部署的选项：

```
private Integer port;

prot = 8081;
DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));

vertx.deployVerticle(UseConfit.class.getName(), options, context.asyncAssertSuccess());
```

这个DeploymentOptions对象可以让我们定义多个参数。并且，它会帮我们将JsonObject注入到verticle的config()方法中。

这并没有真正的解决我们的问题，如果8081端口也被占用了的话。我们现在需要使用一个随机的端口。
```
ServerSocket socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();
```

这个主意是很简单的。我们打开了一个server socket来获取一个随机的端口（这也是为什么将0作为参数）。
我们检索被占用的端口，并且关闭socket。需要注意的是，这个方法并不完美，它也有可能会失败，如果选择的端口在socket.close()方法之后和HTTP服务器启动之前被占用的话。
然而，在大多数的情况下这个方法还是能很好的工作。

## 外部配置-让我们在另一个端口运行

从这里，测试类变成UseConfig2。

在生产环境中，随机端口并不是我们想要的。所以，在实际执行应用的时候，我们需要将配置写在一个外部的文件里。这个配置的文件使用json格式。

创建 o2UseConfig.json，并写下这些内容：
```
{
    "http.port" : 8082
}
```

现在，通过执行下面这句命令，在启动应用的时候，加载这个配置文件:
```
java -jar <jar-name> -conf <conf-name>
```

本项目运行目录:
```
在项目根目录下
java -jar target/java.vertx-1.0-SNAPSHOT-fat.jar -conf src/main/resources/o2UseConfig.json

运行没成功 -_-!!! 报错 : io.vertx.core.impl.NoStackTraceThrowable
```



编辑完毕JSON文件后，即可重新打包运行。打开浏览器，然后访问 [http://localhost:8082](http://localhost:8082)，即可看到应用的界面。


这是怎么工作的呢？记住，我们的fat jar是使用Starter类来加载我们的应用程序。当部署verticle的时候，这个类会读取-conf参数，相应的会创建一个DeploymentOptions对象。