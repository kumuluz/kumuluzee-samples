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

import com.kumuluz.ee.amqp.common.annotations.AMQPConnection;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * @author Blaž Mrak
 * @since 1.0.0
 */

@ApplicationScoped
public class ConnectionInitializer {

    private static Logger log = Logger.getLogger(ConnectionInitializer.class.getName());

    public ConnectionInitializer(){}

    @AMQPConnection
    public Map<String, Connection> localhostConnection(){
        Map<String, Connection> localhost = new HashMap<>();

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        Connection connection = null;

        try {
            connection = connectionFactory.newConnection();
        } catch (IOException | TimeoutException  e) {
            log.severe("Connection could not be created");
        }

        localhost.put("MQtest2", connection);
        return localhost;
    }
}
