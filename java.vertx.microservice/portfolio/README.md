# portfolio

## PortfolioService 接口

### annotation

1. ProxyGen - 可以让Vert.x Codegen自动生成Event Bus服务代理
2. VertxGen - 可以让Vert.x Codegen生成其它Vert.x支持的语言中的服务代理

### getPortfolio 方法

可以让你以异步的方式获取 Portfolio 对象。这个函数是异步的，所以接受一个 Handler 参数，
并且这个Handler的类型参数为 AsyncResult<Portfolio> 。其他函数也是同样的模式。

> 注意：你可能会注意到 package-info.java 这个包描述类文件。在项目中我们需要这个类来为生成服务代理类提供支持。

## portfolio 对象

Portfolio 对象是一个数据对象(data object)。Event Bus代理仅支持少数类型的数据，对于那些不支持的数据类型，
必须使用 @DataObject 注解作为约束。数据对象是遵循下面一系列约束的实体类：

1. 必须加上 @DataObject 注解
2. 必须有一个空的构造函数、一个拷贝构造函数以及一个接受JsonObject 类型参数（必须代表当前对象）的构造函数
3. 类成员必须都有 getter 和 setter

Portfolio 这个类所有的JSON数据处理都由 converters 进行管理，而 converters 是Vert.x自动生成的，
所以其实一个数据对象和一个Java Bean差不多。

## 实现服务

既然我们的服务有了一个异步的接口了，现在是时候来实现具体的逻辑了。在这个服务重，我们要实现三个方法：

1. getPortfolio：通过这个方法，我们可以了解到如何去创建 AsyncResult 对象
2. sendActionOnTheEventBus：通过这个方法，我们可以了解到如何向Event Bus发送消息
3. evaluate：用于计算当前投资组合的价值


