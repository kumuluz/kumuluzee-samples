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

package com.kumuluz.ee.samples.amqp.rabbitmq.messaging;

import com.kumuluz.ee.amqp.common.annotations.AMQPConsumer;
import com.kumuluz.ee.amqp.common.annotations.AMQPProducer;
import com.kumuluz.ee.amqp.rabbitmq.utils.consumer.MessageInfo;

import javax.enterprise.context.ApplicationScoped;
import java.util.logging.Logger;

/**
 * @author Blaž Mrak
 * @since 3.2.0
 */

@ApplicationScoped
public class MessageConsumer {

    private static Logger log = Logger.getLogger(MessageConsumer.class.getName());

    @AMQPConsumer(host = "MQtest", exchange = "directExchange", key = "red")
    public void listenToRed(String message) {
        log.info("Recieved message: " + message + " from direct exchange with the red key.");
    }

    @AMQPConsumer(host = "MQtest", exchange = "directExchange", key = "object")
    public void listenToObject(ExampleObject message) {
        log.info("Recieved message: \"" + message.getContent() + "\" of length " + message.getLength());
    }

    @AMQPConsumer(host = "MQtest", exchange = "directExchange", key = "message")
    @AMQPProducer(host = "MQtest", exchange = "directExchange", key = "red")
    public String listenToObjectMessage(ExampleObject message, MessageInfo messageInfo) {
        log.info("[" + messageInfo.getProperties().getTimestamp().toString() + "] Recieved message: " + message.getContent() + " of length " + message.getLength() + " with title: '" + messageInfo.getProperties().getHeaders().get("title") + "'");
        return "WOW! I can receive and send messages";
    }

    @AMQPConsumer(host = "MQtest2", key = "testQueue")
    public void listenToWorkQueue(ExampleObject message) {
        log.info(" Recieved message: " + message.getContent() + " of length " + message.getLength());
    }
}
