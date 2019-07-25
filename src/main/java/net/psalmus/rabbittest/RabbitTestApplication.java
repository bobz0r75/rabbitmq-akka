/*
 * Copyright © Psalmus.net 2017 - 2019.
 *
 * All rights reserved.
 * Unauthorized using, copying, distributing or providing this program`s
 * source code is prohibited.
 */
package net.psalmus.rabbittest;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.AllDirectives;
import akka.stream.ActorMaterializer;
import com.typesafe.config.Config;
import net.psalmus.rabbittest.actor.MessageLoggingActor;
import net.psalmus.rabbittest.component.RabbitMQConnector;

/**
 * Application for testing Akka and RabbitMQ together
 *
 * @author Bence DEMETER
 * @since 2019/05/16
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public final class RabbitTestApplication extends AllDirectives {

    /**
     * Configuration parameter for host binding
     */
    private static final String HOST_BINDING_PARAM = "binding.host";

    /**
     * Configuration parameter for port binding
     */
    private static final String PORT_BINDING_PARAM = "binding.port";

    /**
     * The Akka actor system
     */
    private ActorSystem actorSystem;

    /**
     * The logging adapter
     */
    private LoggingAdapter log;

    /**
     * The application configuration
     */
    private Config config;

    /**
     * Instantiates the application
     */
    private RabbitTestApplication() {
        this.actorSystem = ActorSystem.create();
        this.config = actorSystem.settings().config();
        this.log = Logging.getLogger(actorSystem, this);
    }

    /**
     * Starts the application
     *
     * @param args The command-line arguments
     */
    public static void main(String[] args) {
        new RabbitTestApplication().start();
    }

    private void start() {
        log.info("Starting RabbitMQ testing application");
        var loggingActor = actorSystem.actorOf(MessageLoggingActor.props(), "messageLogger");
        var bindingHost = config.getString(HOST_BINDING_PARAM);
        var bindingPort = config.getInt(PORT_BINDING_PARAM);
        var mat = ActorMaterializer.create(actorSystem);
        var routeFlow = pathSingleSlash(() -> complete("OKAY")).flow(actorSystem, mat);
        var binding = Http.get(actorSystem).bindAndHandle(routeFlow, ConnectHttp.toHost(bindingHost, bindingPort), mat);
        var rabbitMQConnector = new RabbitMQConnector(config, loggingActor);
        try {
            rabbitMQConnector.connect();
            log.info("Server online at http://" + bindingHost + ":" + bindingPort + "\n\n"
                + "Press RETURN to stop...");
            System.in.read();
            binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> actorSystem.terminate())
                .thenRun(rabbitMQConnector::shutDown);
        } catch (Exception e) {
            log.error("An error has occurred", e);
        }
    }

}
