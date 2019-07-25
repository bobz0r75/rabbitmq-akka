/*
 * Copyright © Psalmus.net 2017 - 2019.
 *
 * All rights reserved.
 * Unauthorized using, copying, distributing or providing this program`s
 * source code is prohibited.
 */
package net.psalmus.rabbittest.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a test message in RabbitMQ
 *
 * @author Bence DEMETER
 * @since 2019/05/16
 */
public class RabbitMQMessage {

    private String id;
    private String message;

    /**
     * Instantiates the object with all arguments
     *
     * @param id The ID of the message
     * @param message The body of the message
     */
    @JsonCreator
    public RabbitMQMessage(@JsonProperty("id") String id, @JsonProperty("message") String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
