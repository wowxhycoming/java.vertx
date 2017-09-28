# 介绍

抛开业务 和 redis 操作不管的话。

# EventBus 注册服务 和 创建代理

## CodeGen

1. @DataObject(generateConverter = true)

生成数据对象代码

2. @ProxyGen

生成 EventBus 代理代码

3. @VertxGen

生成其他支持语言的 @ProxyGen 代码

4. @Fluent

注册服务到 EventBus 的时候，代码生成需要使用的注解，使每个方法都返回 this 。 

## 注册服务

```
ProxyHelper.registerService(JobService.class, vertx, jobService, EB_JOB_SERVICE_ADDRESS);
```

## 创建代理

```
JobService jobSevice = ProxyHelper.createProxy(JobService.class, vertx, address);
jobSevice.<function>
```

# 注意

注册EventBus服务的项目 要与 使用EventBus代理服务的项目 做集群，这样才能通过EventBus交互。

# 启动 jar

```
java -jar java.vertx.blueprint.kue.core-1.0-SNAPSHOT-fat.jar -cluster -ha -conf ./conf.json
```

如果启动报找不到 ClusterManager 的错误，在 pom 添加 hazelcast 的依赖可以解决。

```
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-hazelcast</artifactId>
</dependency>
```

# 其他可能出现的问题

## No handlers for address vertx.kue.service.job.internal

1. 可能是 EventBus 上，真的没有注册到这个地址的服务。
2. EventBus没有集群，所以找不到注册在这个地址上的服务。