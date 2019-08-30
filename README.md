1. java.vertx.meet , helloworld (10101)
2. java.vertx.web ,  web project (10201)
3. java.vertx.blueprint.todo , a rest web project (10301)
    - 数据源 的异步操作
    - 数据源连接 的统一处理
    - 数据对象 的规范 和 数据对象转换类
4. java.vertx.blueprint.kue (10401)
    - 消息、消息系统以及事件驱动的运用
    - Vert.x Event Bus 的几种事件机制（发布/订阅、点对点模式）
    - 设计 分布式 的Vert.x应用
    - Vert.x Service Proxy（服务代理，即异步RPC）的运用，在 Event Bus 上发布代理
    
    > 没有把 service consumer 和 service provider 从物理结构上分开，必须在一个项目里，很low很不适很不解。
    因为 consumer 必须持有 provider interface 引用，而 interface 还直接引用了 impl 创建服务实例 和 服务代理。
    consumer 必须跟 provider interface 在一个项目， provider interface 必须跟 impl 在一个项目，意义呢？
    没有 EventBus 就不能直接达到异步调用的目的？
    
    > 在群里跟大拿交流了，自己推敲出的结论，希望后期理解的更深，可以找到新出路：
    注册到eb，是为了注册discovery，而注册了discovery，
    还需要再专门提供一层 RestApi 或者 MessageApi 来专门暴露真正的服务。 
    这样才能只传递数据对象，而不涉及到对服务的直接调用，才能从物理上分开。
    


?. java.vertx.microservice