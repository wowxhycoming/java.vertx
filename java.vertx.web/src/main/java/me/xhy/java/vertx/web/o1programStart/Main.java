package me.xhy.java.vertx.web.o1programStart;

import com.sun.istack.internal.NotNull;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.spi.VertxFactory;
import io.vertx.ext.web.Router;
import me.xhy.java.vertx.web.util.Util;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Created by xuhuaiyu on 2016/11/13.
 */
public class Main {

	public static void main(final String... args) {

		System.out.println(Router.class.getName());
		System.out.println(Runtime.getRuntime().availableProcessors());

		final VertxOptions vertxOptions = Util.readOpts("VXWEB.");
		final VertxFactory factory = new VertxFactoryImpl();
		final Vertx vertx = factory.vertx(vertxOptions);
		final DeploymentOptions verticleOpts = Util.readOpts();
		vertx.deployVerticle(RouterVerticle.class.getName(), verticleOpts);

	}




}
