package com.kumuluz.ee.samples.jms;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Dejan Ognjenović
 * @since 2.4.0
 */
public class QueueHandler {

    private static Logger LOG = Logger.getLogger(QueueHandler.class.getName());

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    private static String queueName = "KUMULUZ_QUEUE";

    private static int timeout = 1000;

    public static void addToQueue(Customer customer) {

        // Create connection factory and allow all packages for test purpose
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        // Not recommended to trust all packages, only use for testing purposes
        connectionFactory.setTrustAllPackages(true);
        Connection connection;

        try {
            // Create connection
            connection = connectionFactory.createConnection();
            connection.start();

            // create session and producer
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(destination);

            // Create an serializable object to send to queue
            ObjectMessage msg = session.createObjectMessage();
            msg.setObject(customer);
            msg.setJMSType(Customer.class.getName());

            // Sending to queue
            producer.send(msg);

            connection.close();
        } catch (JMSException e) {
            LOG.log(Level.SEVERE ,"JMS threw an error.", e);
        }

    }

    public static Customer readFromQueue() {

        // Create connection factory and allow all packages for test purpose
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        // Not recommended to trust all packages, only use for testing purposes
        connectionFactory.setTrustAllPackages(true);
        Connection connection;

        Customer customer = null;

        try {
            // Create connection
            connection = connectionFactory.createConnection();
            connection.start();

            // create session and consumer
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(destination);

            // retrieve message
            Message message = consumer.receive(timeout);

            // check if correct type and cast message to Customer
            if (message instanceof ObjectMessage && Customer.class.getName().equals(message.getJMSType())) {
                ObjectMessage msg = (ObjectMessage) message;
                customer = (Customer) msg.getObject();
            } else if (message == null) {
                LOG.log(Level.INFO ,"Queue " + queueName +" is empty.");
            } else {
                LOG.log(Level.INFO ,"Message was not the right type.");
            }

            connection.close();
        } catch (JMSException e) {
            LOG.log(Level.SEVERE ,"JMS threw an error.", e);
        }

        return customer;
    }
}
