package com.kumuluz.ee.samples.jms;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Dejan Ognjenović
 * @since 2.4.0
 */
public class QueueHandler {

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    private static String queueName = "KUMULUZ_QUEUE";

    public static void addToQueue(Customer customer) {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connectionFactory.setTrustAllPackages(true);
        Connection connection;
        
        try {
            connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(destination);

            ObjectMessage msg = session.createObjectMessage();
            msg.setObject(customer);
            msg.setJMSType(Customer.class.getName());

            producer.send(msg);

            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public static Customer readFromQueue() {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connectionFactory.setTrustAllPackages(true);
        Connection connection;
        Customer customer = null;

        try {

            connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(destination);

            Message message = consumer.receive();

            if (message instanceof ObjectMessage) {
                ObjectMessage msg = (ObjectMessage) consumer.receive();
                customer = (Customer) msg.getObject();
            } else {

            }

            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }

        return customer;
    }
}
