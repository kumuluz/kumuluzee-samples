# KumuluzEE AMQP sample with RabbitMQ

> Develop messaging with RabbitMQ and pack it as a KumuluzEE microservice.

The objective of this sample is to demonstrate how to develop messaging using RabbitMQ. The tutorial guides you through the development of RabbitMQ publisher and consumer. You will add KumuluzEE dependencies into pom.xml. You will recieve messages through your REST service and will then send them to the RabbitMQ broker, which will deliver them to appropriate consumers.

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
3. RabbitMQ:
    * If you have installed RabbitMQ, you can check the version by typing the following in a RabbitMQ command line:
    
        ```bash
        $ rabbitmqctl status
        ```
		
	* Or run RabbitMQ with docker:
	
	```bash
        $ docker run -d --hostname my-rabbit --name some-rabbit -p 5672:5672 rabbitmq:3
        ```

## Usage

The example uses maven to build and run the microservices.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-amqp-rabbitmq
    $ mvn clean package
    ```

2. Run the sample:
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
* JAX-RS REST resource page - http://localhost:8080/v1/

To shut down the example simply stop the processes in the foreground.

## Tutorial
This tutorial will guide you through the steps required to create a simple Messaging microservice which uses RabbitMQ and pack it as a KumuluzEE microservice. We will extend the existing [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs)
 with RabbitMQ messaging methods. Therefore, first complete the existing JAX-RS sample tutorial, or clone the JAX-RS sample code.

We will follow these steps:
* Complete the tutorial for [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) or clone the existing sample
* Ensure access to RabbitMQ broker
* Add Maven dependencies
* Implement messaging using RabbitMQ
* Build the microservice
* Run it

### Add Maven dependencies

Since your existing starting point is the existing KumuluzEE JAX-RS REST sample, you should already have the dependencies for `kumuluzee-bom`, `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-jax-rs-jersey` configured in the `pom.xml`.

Add the `kumuluzee-cdi-weld` and `kumuluzee-amqp-rabbitmq` dependencies:
```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-cdi-weld</artifactId>
</dependency>
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-amqp-rabbitmq</artifactId>
    <version>${kumuluzee.rabbitmq.version}</version>
</dependency>
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

### Configure RabbitMQ broker

In the directory `resources` edit the file `config.yaml` by adding the following RabbitMQ properties:

```yaml
kumuluzee:
  amqp:
    rabbitmq:
      hosts:
        - name: MQtest
          url: localhost
          exchanges:
            - name: directExchange
              type: direct
        - name: MQtest2
          queues:
          - name: testQueue
      properties:
        - name: testProperty
          headers:
            title: text
          timestamp: true
```

### Implement message producer
Create a new class called `MessageProducer` and inject a RabbitMQ channel into it with the `@AMQChannel` annotation.
Then we can use `RestMessage` parameters to publish a message to a RabbitMQ broker.
```java
@ApplicationScoped
public class MessageProducer {
    
    @Inject
    @AMQChannel("MQtest")
    private Channel channel;

    public String sendRestMessage(RestMessage message) {
        try {
            channel.basicPublish(message.exchange, message.key, null, message.message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message.message;
    }
```
This is not that convenient, as we have to have a specific structure that we are getting our data from. Another way of sending data is with `@AMQPProducer` annotation. All we need to do is to return the object we want to send. In this example we are sending a string "I'm red".
```java
    @AMQProducer(host="MQtest", exchange="directExchange", key="red", properties="textPlain")
    public String sendRedMessage() {
        return "I'm red!";
    }
```
Instead of returning any object, we could return a specific object Message, to which we can set the host, exchange, keys, body and properties, which we cannot predefine. In this example our method will create a random number and based on it, it will choose to which consumer it will send a message. It is also good to know that Message parameters will override annotation parameters.
```java
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
```
### Implement message consumer
Create a new class `MessageConsumer` and annotate it with `@ApplicationScoped` annotation. Then create a new method, annotate it with `@AMQPConsumer` and add ConsumerMessage parameter to it. 

After we have created our method, we can print out the message we recieved.
```java
@ApplicationScoped
public class QueueHandler {
    
    @AMQConsumer(host="MQtest", exchange = "directExchange", key = "red")
    public void listenToRed(ConsumerMessage consumerMessage){
        String message = (String) consumerMessage.getBody();
        System.out.println("Recieved message: " + message + " from direct exchange with the red key.");
    }
```

### Implement REST Service

Create a new REST object `RestMessage` which we will use to get information from the HTTP request.

* Add fields `String exchange`, `String key` and `String message`.

```java
public class RestMessage {
    public String exchange;
    public String key;
    public String message;
}
```

Make `MessageResource` class a CDI bean by adding `@ApplicationScoped` annotation. Then create three endpoints which we will use to send messages.

```java
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {

    @Inject
    @AMQPChannel("MQtest")
    private Channel channel;

    @Inject
    private MessageProducer messageProducer;

    @POST
    public void messageToSend(RestMessage message){
        try {
            channel.basicPublish(message.exchange, message.key, null, message.message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   @GET
    @Path("/red")
    public Response getRed(){
        messageProducer.sendRed();
        return Response.ok("Red message sent.").build();
    }

    @GET
    @Path("/object")
    public Response getObject(){
        messageProducer.sendObject();
        return Response.ok("Object message sent.").build();
    }

    @GET
    @Path("/message")
    public Response getMessageObjectCustomProperty(){
        messageProducer.sendObjectMessageCustomProperty();
        return Response.ok("Object message with custom properties sent.").build();
    }

    @GET
    @Path("/queue")
    public Response getMessageQueue(){
        messageProducer.sendToQueue();
        return Response.ok("Object message with custom properties sent.").build();
    }

    @GET
    @Path("/fullMessage")
    public Response getFullMessage(){
        messageProducer.sendFullMessage();
        return Response.ok("Object message sent to a random consumer.").build();
    }
```

Example requests:  
- POST: http://localhost:8080/v1/ Body:
```json
{
    "exchange": "directExchange", 
    "key": "red", 
    "message": "This is a message."
}
```
- GET: http://localhost:8080/v1/red  
- GET: http://localhost:8080/v1/object  
- GET: http://localhost:8080/v1/message  
- GET: http://localhost:8080/v1/queue  
- GET: http://localhost:8080/v1/fullMessage  

### Configure CDI

Create the directory `resources/META-INF`. In this directory create the file `beans.xml` with the following content to enable CDI:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/beans_1_2.xsd"
       bean-discovery-mode="annotated">
</beans>
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
