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

import com.kumuluz.ee.amqp.common.annotations.AMQPProducer;
import com.kumuluz.ee.amqp.rabbitmq.utils.producer.Message;
import com.rabbitmq.client.MessageProperties;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Blaž Mrak
 * @since 1.0.0
 */

@ApplicationScoped
public class MessageProducer {

    @AMQPProducer(host="MQtest", exchange = "directExchange", key = "red", properties="textPlain")
    public String sendRed(){
        return "I'm Red!";
    }

    @AMQPProducer(host="MQtest", exchange = "directExchange", key = "object")
    public ExampleObject sendObject(){
        ExampleObject exampleObject = new ExampleObject();
        exampleObject.setContent("I'm just an object.");
        return exampleObject;
    }

    @AMQPProducer(host="MQtest", exchange = "directExchange", key = "message", properties = "testProperty")
    public Message sendObjectMessageCustomProperty(){
        Message message = new Message();
        ExampleObject exampleObject = new ExampleObject();
        exampleObject.setContent("I'm an object in a message with custom properties.");
        return message.body(exampleObject);
    }

    @AMQPProducer(host="MQtest2", key = "testQueue")
    public Message sendToQueue(){
        Message message = new Message();
        ExampleObject exampleObject = new ExampleObject();
        exampleObject.setContent("I'm an object in a message");
        return message.body(exampleObject).basicProperties(MessageProperties.BASIC);
    }

    @AMQPProducer
    public Message sendFullMessage(){
        Message message = new Message();
        ExampleObject exampleObject = new ExampleObject();
        exampleObject.setContent("I'm an object in a special message");

        if(Math.random() < 0.5){
            message.host("MQtest")
                    .key(new String[]{"object"})
                    .exchange("directExchange")
                    .basicProperties(MessageProperties.BASIC);
        } else {
            message.host("MQtest2")
                    .key(new String[]{"testQueue"})
                    .basicProperties("testProperty");
        }

        return message.body(exampleObject);
    }
}
