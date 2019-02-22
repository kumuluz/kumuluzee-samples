/*
 *  Copyright (c) 2014-2019 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kumuluz.ee.samples.amqp.rabbitmq.api;

import com.kumuluz.ee.amqp.common.annotations.AMQPChannel;
import com.kumuluz.ee.samples.amqp.rabbitmq.messaging.MessageProducer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author Blaž Mrak
 * @since 3.2.0
 */

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class QueueResource {
	
    @Inject
    private MessageProducer messageProducer;

    @POST
    public Response messageToSend(RestMessage message) {
        messageProducer.sendRestMessage(message);
        return Response.ok("\"RestMessage sent.\"").build();
    }

    @GET
    @Path("/red")
    public Response getRed() {
        messageProducer.sendRed();
        return Response.ok("\"Red message sent.\"").build();
    }

    @GET
    @Path("/object")
    public Response getObject() {
        messageProducer.sendObject();
        return Response.ok("\"Object message sent.\"").build();
    }

    @GET
    @Path("/message")
    public Response getMessageObjectCustomProperty() {
        messageProducer.sendObjectMessageCustomProperty();
        return Response.ok("\"Object message with custom properties sent.\"").build();
    }

    @GET
    @Path("/queue")
    public Response getMessageQueue() {
        messageProducer.sendToQueue();
        return Response.ok("\"Object message with custom properties sent.\"").build();
    }

    @GET
    @Path("/fullMessage")
    public Response getFullMessage() {
        messageProducer.sendFullMessage();
        return Response.ok("\"Object message sent to a random consumer.\"").build();
    }
}
