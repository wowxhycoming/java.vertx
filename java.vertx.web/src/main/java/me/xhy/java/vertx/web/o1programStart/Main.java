package me.xhy.java.vertx.web.o1programStart;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.spi.VertxFactory;
import io.vertx.ext.web.Router;
import me.xhy.java.vertx.web.util.Options;

/**
 * Created by xuhuaiyu on 2016/11/13.
 */
public class Main {

    public static void main(final String... args) {

        System.out.println(Router.class.getName());
        System.out.println(Runtime.getRuntime().availableProcessors());

        final VertxOptions vertxOptions = Options.getVertxOptions("VXWEB.");
        final VertxFactory factory = new VertxFactoryImpl();
        /**
         * 因为启用了 cluster 集群模式，所以不能直接使用 factory.vertx() 方法来创建 Vert.x 实例
         * 所以关闭了 cluster 集群模式，opts.setClustered(false)
         */
        final Vertx vertx = factory.vertx(vertxOptions);
        final DeploymentOptions verticleOpts = Options.standardDeploymentOptions();
        vertx.deployVerticle(RouterVerticle.class.getName(), verticleOpts);

    }

}
