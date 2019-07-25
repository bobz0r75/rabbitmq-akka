/*
 * Copyright © Psalmus.net 2017 - 2019.
 *
 * All rights reserved.
 * Unauthorized using, copying, distributing or providing this program`s
 * source code is prohibited.
 */
package net.psalmus.rabbittest.component;

/**
 * Contains constants for configuring RabbitMQ connection
 *
 * @author Bence DEMETER
 * @since 2019/05/17
 */
final class RabbitMQConstants {

    /**
     * Parameter name for RabbitMQ hostname
     */
    static final String RABBIT_MQ_HOST = "rabbitMq.host";

    /**
     * Parameter name for RabbitMQ port
     */
    static final String RABBIT_MQ_PORT = "rabbitMq.port";

    /**
     * Parameter name for RabbitMQ password
     */
    static final String RABBIT_MQ_PASS = "rabbitMq.user.password";

    /**
     * Parameter name for RabbitMQ username
     */
    static final String RABBIT_MQ_USER = "rabbitMq.user.username";

    /**
     * Parameter name for RabbitMQ consumer tag
     */
    static final String RABBIT_MQ_CONSUMER_TAG = "myConsumerTag";

    /**
     * Parameter name for RabbitMQ routing key
     */
    static final String RABBIT_MQ_ROUTING_KEY = "rabbitMq.routingKey";

    /**
     * Parameter name for RabbitMQ queue name
     */
    static final String RABBIT_MQ_QUEUE = "rabbitMq.queue";

    /**
     * Parameter name for RabbitMQ exchange name
     */
    static final String RABBIT_MQ_EXCHANGE = "rabbitMq.exchange";

    /**
     * Hides the default constructor
     */
    private RabbitMQConstants() {
        // Nothing to do
    }

}
