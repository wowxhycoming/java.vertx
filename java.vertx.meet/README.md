# 运行方法

## 安装 vert.x

1. 下载 vert.x ，解压。
2. JAVA_HOME 为 1.8版本。
3. VERTX_HOME 为解压根目录。
4. Path 中增加 %VERTX_HOME%\bin 。

## 打包

用 maven 打包本项目， maven 会根据该项目中的 pom.xml 进行打包。

## 运行

进入 target 目录

java -jar <jar-name>-fat.jar

## 日志

### 系统内日志框架

src/main/resources 目录下放置 vertx-default-jul-logging.properties 文件   JUL 标准

### Log4j

jar包放到classpath下， src/main/resources 下放置log4j配置文件 

```
java.util.logging.ConsoleHandler.level=INFO
java.util.logging.FileHandler.level=INFO
```

