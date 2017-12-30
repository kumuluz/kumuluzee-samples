# KumuluzEE JMS ActiveMQ sample

> Develop a basic JMS client within a REST service and pack it as a KumuluzEE microservice.

The objective of this sample is to demonstrate how to use KumuluzEE JMS module. The tutorial guides you through the development of a simple JMS client and will show how to send an object to a queue and how to retrieve it. The methods that will send and receive messages will be exposed via a REST service. Required knowledge: basic familiarity with JMS and basic concepts of REST and JSON.

## Requirements

In order to run this example you will need the following:

1. Java 8 (or newer), you can use any implementation:
    * If you have installed Java, you can check the version by typing the following in a command line:
        
        ```
        java -version
        ```

2. Maven 3.2.1 (or newer):
    * If you have installed Maven, you can check the version by typing the following in a command line:
        
        ```
        mvn -version
        ```
3. Git:
    * If you have installed Git, you can check the version by typing the following in a command line:
    
        ```
        git --version
        ```
4. ActiveMQ:
    * ActiveMQ can be downloaded on the following 
        [link](http://activemq.apache.org/download.html)


## Prerequisites

This sample does not contain any prerequisites and can be started on its own.

## Usage

This sample uses ActiveMQ implementation of JMS.
   
1. Run ActiveMQ
* Unix/Linux:
    ```bash
    $ cd path_to_activemq/bin
    $ activemq start
    ```
* Windows:
    ```batch
    cd "path_to_activemq\bin"
    activemq start
    ```
Default location for ActiveMQ console should be http://localhost:8161/admin

2. The example uses maven to build and run the microservice. Build the sample using maven:

    ```bash
    $ cd jms
    $ mvn clean package
    ```

3. Run the sample:
* Uber-jar:

    ```bash
    $ java -jar target/${project.build.finalName}.jar
    ```
    
    in Windows environemnt use the command
    ```batch
    java -jar target/${project.build.finalName}.jar
    ```

* Exploded:

    ```bash
    $ java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
    in Windows environment use the command
    ```batch
    java -cp target/classes;target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
    
The application/service can be accessed on the following URL:
* JAX-RS REST resource - http://localhost:8080/v1/customers


To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a  simple JMS client using ActiveMQ and pack it as a KumuluzEE microservice. 
We will develop a simple Customer REST service with the following resources:
* GET http://localhost:8080/v1/customers – get customer from ActiveMQ
* POST http://localhost:8080/v1/customers – add a customer to ActiveMQ

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, JMS and JAX-RS)
* Implement the JMS client
* Implement the service using standard JAX-RS 2 API
* Build the microservice
* Run it

### Add Maven dependencies

Add the KumuluzEE BOM module dependency to your `pom.xml` file:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-bom</artifactId>
            <version>${kumuluz.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty`, `kumuluzee-jax-rs-jersey` and `kumuluzee-jms-activemq` dependencies:
```xml
<dependencies>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
         <artifactId>kumuluzee-core</artifactId>
    </dependency>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
         <artifactId>kumuluzee-servlet-jetty</artifactId>
    </dependency>
    <dependency>
         <groupId>com.kumuluz.ee</groupId>
         <artifactId>kumuluzee-jax-rs-jersey</artifactId>
    </dependency>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
        <artifactId>kumuluzee-jms-activemq</artifactId>
    </dependency>
</dependencies>
```

Add the `kumuluzee-maven-plugin` build plugin to package microservice as uber-jar:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-maven-plugin</artifactId>
            <version>${kumuluzee.version}</version>
            <executions>
                <execution>
                    <id>package</id>
                    <goals>
                        <goal>repackage</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

or exploded:

Add the `kumuluzee-maven-plugin` build plugin to package microservice as exploded:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-maven-plugin</artifactId>
            <version>${kumuluzee.version}</version>
            <executions>
                <execution>
                    <id>package</id>
                    <goals>
                        <goal>copy-dependencies</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Implement the REST service

Register your module as JAX-RS service and define the application path. You could do that in web.xml or for example with `@ApplicationPath` annotation:

```java
@ApplicationPath("v1")
public class CustomerApplication extends Application {
}
```

Implement JAX-RS resource, for example, to implement resource `customers`. The POST method is going to send the input customer directly to JMS queue while the GET method is going to retrieve the next pending `Customer` object from queue:

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
public class CustomerResource {

    @POST
    public Response addCustomerToQueue(Customer customer) {
        QueueHandler.addToQueue(customer);
        return Response.noContent().build();
    }

    @GET
    public Response readCustomerFromQueue() {
        Customer customer = QueueHandler.readFromQueue();
        return customer != null
                ? Response.ok(customer).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }
}
```

Implement the `Customer` Java class, which is a POJO. The class must implement the Serializable interface so we can send this object directly in queue:
```java
public class Customer implements Serializable {

    private String id;

    private String firstName;

    private String lastName;

    // TODO: implement get and set methods
}
```

The JMS client logic will be implemented in `QueueHandler`. A sample implementation of this client, can be implemented as follows:

```java
public class QueueHandler {
    
    private static Logger LOG = Logger.getLogger(QueueHandler.class.getName());

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    private static String queueName = "KUMULUZ_QUEUE";

    public static void addToQueue(Customer customer) {

        // Create connection factory and allow all packages for test purpose
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
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
            Message message = consumer.receive();

            // check if correct type and cast message to Customer
            if (message instanceof ObjectMessage && Customer.class.getName().equals(message.getJMSType())) {
                ObjectMessage msg = (ObjectMessage) consumer.receive();
                customer = (Customer) msg.getObject();
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
```

Method `addToQueue` takes the input customer object and sends it to queue while the `readFromQueue` read the next pending object from queue.


### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
