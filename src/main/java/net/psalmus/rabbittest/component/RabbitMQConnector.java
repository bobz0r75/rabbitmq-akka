/*
 * Copyright © Psalmus.net 2017 - 2019.
 *
 * All rights reserved.
 * Unauthorized using, copying, distributing or providing this program`s
 * source code is prohibited.
 */
package net.psalmus.rabbittest.component;

import static com.rabbitmq.client.BuiltinExchangeType.DIRECT;
import static net.psalmus.rabbittest.component.RabbitMQConstants.RABBIT_MQ_CONSUMER_TAG;
import static net.psalmus.rabbittest.component.RabbitMQConstants.RABBIT_MQ_EXCHANGE;
import static net.psalmus.rabbittest.component.RabbitMQConstants.RABBIT_MQ_HOST;
import static net.psalmus.rabbittest.component.RabbitMQConstants.RABBIT_MQ_PASS;
import static net.psalmus.rabbittest.component.RabbitMQConstants.RABBIT_MQ_PORT;
import static net.psalmus.rabbittest.component.RabbitMQConstants.RABBIT_MQ_QUEUE;
import static net.psalmus.rabbittest.component.RabbitMQConstants.RABBIT_MQ_ROUTING_KEY;
import static net.psalmus.rabbittest.component.RabbitMQConstants.RABBIT_MQ_USER;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.typesafe.config.Config;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;
import net.psalmus.rabbittest.domain.RabbitMQMessage;

/**
 * Responsible for connecting to RabbitMQ
 *
 * @author Bence DEMETER
 * @since 2019/05/16
 */
public class RabbitMQConnector {

    private final ObjectMapper mapper = new ObjectMapper();
    private Config config;
    private ActorRef loggingActor;
    private Connection connection;
    private Channel channel;

    /**
     * Instantiates the object
     *
     * @param config The application configuration
     * @param loggingActor The actor which handles and log the messages
     */
    public RabbitMQConnector(Config config, ActorRef loggingActor) {
        this.config = config;
        this.loggingActor = loggingActor;
    }

    /**
     * Shuts down the connector
     */
    public void shutDown() {
        try {
            connection.close();
        } catch (IOException e) {
            // Nothing to do
        }
    }

    /**
     * Connects to a RabbitMQ server
     *
     * @throws IOException When a connection error occurring
     * @throws TimeoutException When a connection timeout occurring
     */
    public void connect() throws IOException, TimeoutException {
        var factory = getConnectionFactory();
        connection = factory.newConnection();
        channel = connection.createChannel(3);
        var exchange = config.getString(RABBIT_MQ_EXCHANGE);
        channel.exchangeDeclare(exchange, DIRECT, true);
        var queueName = channel.queueDeclare(config.getString(RABBIT_MQ_QUEUE),
            false, false, false, new HashMap<>()).getQueue();
        channel.queueBind(queueName, exchange, config.getString(RABBIT_MQ_ROUTING_KEY));
        channel.basicConsume(queueName, false, RABBIT_MQ_CONSUMER_TAG, getMessageConsumer());
    }

    private ConnectionFactory getConnectionFactory() {
        var factory = new ConnectionFactory();
        factory.setUsername(config.getString(RABBIT_MQ_USER));
        factory.setPassword(config.getString(RABBIT_MQ_PASS));
        factory.setHost(config.getString(RABBIT_MQ_HOST));
        factory.setPort(config.getInt(RABBIT_MQ_PORT));
        factory.setVirtualHost("/");
        return factory;
    }

    private DefaultConsumer getMessageConsumer() {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
                byte[] body) throws IOException {
                long deliveryTag = envelope.getDeliveryTag();
                var contentType = properties.getContentType();
                handleMessageBody(body);
                channel.basicAck(deliveryTag, false);
            }
        };
    }

    private void handleMessageBody(byte[] body) {
        var value = new String(body);
        try {
            var rabbitMQMessage = mapper.readValue(value, RabbitMQMessage.class);
            loggingActor.tell(rabbitMQMessage, ActorRef.noSender());
        } catch (IOException e) {
            // Nothing to do
        }
    }
}
