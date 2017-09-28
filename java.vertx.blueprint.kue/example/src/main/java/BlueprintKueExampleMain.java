import com.hazelcast.config.Config;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.spi.VertxFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import me.xhy.java.vertx.blueprint.kue.core.queue.KueVerticle;
import me.xhy.java.vertx.blueprint.kue.example.LearningVertxVerticle;

/**
 * Created by xuhuaiyu on 2017/9/23.
 * <p>
 * Blueprint Kue Example Main
 */
public class BlueprintKueExampleMain {

    public static void main(String[] args) {

        /*
        final VertxFactory vertxFactory = new VertxFactoryImpl();
        final Vertx vertx = vertxFactory.vertx();

        vertx.deployVerticle(LearningVertxVerticle.class.getName());
        */

        final ClusterManager clusterManager = new HazelcastClusterManager(new Config());

        final VertxOptions vertxOpts = new VertxOptions();
        vertxOpts.setClustered(true);
        vertxOpts.setClusterHost("127.0.0.1");
        vertxOpts.setClusterPort(4052);
        vertxOpts.setClusterManager(clusterManager);

        final VertxFactory vertxFactory = new VertxFactoryImpl();

        vertxFactory.clusteredVertx(vertxOpts, resultHandler -> {
            if (resultHandler.succeeded()) {
                final Vertx vertx = resultHandler.result();
                vertx.deployVerticle(LearningVertxVerticle.class.getName());
            }
        });
    }
}
