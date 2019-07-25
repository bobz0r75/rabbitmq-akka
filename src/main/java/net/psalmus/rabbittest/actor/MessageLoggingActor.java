/*
 * Copyright © Psalmus.net 2017 - 2019.
 *
 * All rights reserved.
 * Unauthorized using, copying, distributing or providing this program`s
 * source code is prohibited.
 */
package net.psalmus.rabbittest.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import net.psalmus.rabbittest.domain.RabbitMQMessage;

/**
 * An actor which logs a RabbitMQ message
 *
 * @author Bence DEMETER
 * @since 2019/05/16
 */
public class MessageLoggingActor extends AbstractActor {

    /**
     * The logging adapter
     */
    private LoggingAdapter log;

    /**
     * Instantiates the actor
     */
    MessageLoggingActor() {
        super();
        log = Logging.getLogger(getContext().getSystem(), this);
    }

    /**
     * Creates the actor props
     *
     * @return The actor props
     */
    public static Props props() {
        return Props.create(MessageLoggingActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(RabbitMQMessage.class, this::logMsg)
            .build();
    }

    private void logMsg(RabbitMQMessage msg) {
        log.info("Message received: {}", msg.getMessage());
    }

}
