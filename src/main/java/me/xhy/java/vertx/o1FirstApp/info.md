# 开始

这个程序很简单，这个类继承了AbstractVerticle。在Vert.x里，一个verticle就是一个组件。通过继承AbstractVerticle，我们的类就能够获取vertx对象并使用了。

当要部署verticle的时候，start方法就会被调用。当然，我们也可以实现一个stop方法，但是在这个例子中，并没有实现这个方法，将由Vert.x来替我们进行垃圾回收。
这个start方法接受一个Future对象，当我们start方法完成了或者报告一个错误的时候，它会通知Vert.x。
Vert.x一个特性就是异步／无阻塞，当我们部署verticle的时候，它并不会等到这个start方法完成才完成部署。因此，这个Future参数对于通知Vert.x,start方法完成是非常重要的。
（小编注：这边有点绕，因为start方法内含有异步回调，这些代码不会马上执行，而Vertx不会等待这些代码的执行，它会一直往下走直到结束）

这个start方法创建了一个HTTP服务器、接受一个请求并处理它。这个处理request请求的是一个lambda表达式，传入一个requestHandler方法，任何时刻，当这个服务器接受一个请求就会调用。
这里，我们会返回一个“hello...”的字符串。最后，这个服务器绑定了8080端口。这可能会失败，因为8080端口可能被占用了。
我们传入了另外一个lambda表达式，来检查这个链接是否成功了。就像前面提到的，当成功的时候，它会调用fut.complete，失败了就调用ful.fail返回一个错误。

# 测试

完成了应用的开发工作是很好的，但是我们可能并没有那么细心，所以需要对它进行测试。这个测试，使用了junit和vertx-unit。vertx-unit是vert.x的一个组件，用来测试vert.x的应用程序将会更加的得心应手。

打开pom.xml文件，然后加上下面两个依赖。
```
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-unit</artifactId>
    <version>3.0.0</version>
    <scope>test</scope>
</dependency>
```

MyFirstVerticleTest ，这是用来测试我们verticle的Junit测试，它使用了vertx-unit，vert.x-unit会让我们的异步测试变的更加容易，同时也是vert.x的基本组件之一。

在setUp方法里，我们创建了一个Vertx的实例并且部署了verticle。你可能会注意到，@Before方法与我们传统的测试方法不一样，它接收一个TestContext参数。
这个对象让我们控制测试程序的异步部分。举例来说，当我们部署verticle的时候，它启动是异步的。在它正确启动之前我们并不能检查什么。
因此，deployVerticle方法的第二个参数，我们传入一个结果的处理者：context.asyncAssertSuccess()。如果verticle没有正确启动，这个测试就失败了。
此外，它会等待直到vertical完成。记住，在我们的verticle中，我们调用fut.complete()。因此，它会等待直到这个方法被调用，而在调用失败的情况下，测试也会失败。

这个tearDown方法就简单了，仅仅是结束我们创建的这个vertx实例。

现在让我们来看看测试程序的这个testMyApplication方法。这个方法向我们的应用程序发出一个请求并且检查结果。发出请求和接收回复是异步。
所以，我们需要一个能够控制异步的方式。就像，setUp和tearDown方法，这个测方法接收一个TestContext参数。
在这个对象中，我们创建了一个异步处理对象async，当测试完成的时候，使用async.complete()帮我们通知测试框架。

所以，一旦async handle被创建了，我们就创建了一个HTTP的客户端并且提交了一个HTTP请求，由我们的应用程序通过调用getNow()（getNow是get(...).end()的缩写）方法来处理。
这个Response是通过lambda表达式来处理的。在lambda表达式中，我们通过传入另一个lambda表达式到handler方法来取得响应。body参数就是响应（作为一个buffer对象）。
我们检查这个响应体包含了字符串"Hello"然后声明测试已经完成了。

现在让我们再来看一下这个assertions。不像传统的junit测试，它使用context.assert...。实际上，当assertion失败，将会立刻打断这个测试。
所以，使用这些assertion方法对测试Vert.x异步应用程序是非常重要的。

# 打包

现在让我们来总结一下。我们有一个应用程序和一个测试程序。现在我们需要来打包这个应用程序。
在这篇文章中，我们将应用程序打包到一个fat.jar里。fat.jar是一个单独的可执行的Jar文件，包含了所有运行这个程序的依赖。把Vert.x应用程序打包成一个文件是很方便的，同时也容易执行。

为了创建这个fat.jar，我们需要编辑pom.xml文件，添加下面这个片段到</plugins>之前。
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>2.3</version>
    <executions>
        <execution>
        <phase>package</phase>
        <goals>
            <goal>shade</goal>
        </goals>
        <configuration>
            <transformers>
                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                <manifestEntries>
                    <Main-Class>io.vertx.core.Starter</Main-Class>
                    <Main-Verticle>io.vertx.blog.first.MyFirstVerticle</Main-Verticle>
                </manifestEntries>
                </transformer>
            </transformers>
            <artifactSet/>
            <outputFile>${project.build.directory}/${project.artifactId}-${project.version}-fat.jar</outputFile>
            </configuration>
        </execution>
    </executions>
</plugin>
```
这里使用maven-shade-plugin插件来创建fat jar。
在manifestEntries里写明了verticle的名字。你可能会奇怪Starter类是怎么来的。它实际上是vert.x的一个类，由它创建vertx实例和部署我们的verticle。

配置好插件，然我们开始运行：

mvn clean package

之后，将会创建target/my-first-app-1.0-SNAPSHOT-fat.jar将所有的依赖都嵌入到了我们的程序之中（包括vert.x本身）。

# 执行

现在，我们有了fat jar，但是我们想要看的我们的程序运行起来。前面说了，由于打包fat jar，运行一个vert.x将会很容易：

java -jar target/{project.artifactId}-1.0-SNAPSHOT-fat.jar

然后，打开浏览器访问 [http://localhost:8080](http://localhost:8080) 。

# 总结

这个Vert.x 3速成教程，向你展现了怎么使用Vert.x 3部署一个简单的应用程序，还有如何测试、打包和运行。现在你已经知道了使用Vert.x 3构建一个非常棒的程序需要的所有基本步骤。

