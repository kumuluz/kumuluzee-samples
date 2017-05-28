# KumuluzEE Kafka &mdash; produce Kafka messages

> Develop a REST service that produces Kafka messages to selected topic

The objective of this sample is to show how to produce Kafka messages with a simple Rest service.
 The tutorial will guide you through all the necessary steps. You will add KumuluzEE dependencies into pom
.xml. You will develop a simple REST service, which uses KumuluzEE Kafka extension for producing messages.
Required knowledge: basic familiarity with JAX-RS and REST; basic familiarity with Apache Kafka.

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

## Prerequisites

To run this sample you will need an Kafka and Zookeeper instance. 
There are a lot of Kafka Docker available on the Docker hub, in this tutorial we use  
[ches/kafka](https://hub.docker.com/r/ches/kafka/) and a separate Docker with the Zookeeper instance [jplock/zookeeper](https://hub.docker.com/r/jplock/zookeeper/)
Here is an example on how to quickly run the Zookeeper and Kafka Docker:

   ```bash
    $ docker run -d -p 2181:2181 --name zookeeper jplock/zookeeper
    $  docker run -d --name kafka --link zookeeper:zookeeper ches/kafka
   ```
## Usage

The example uses maven to build and run the microservice.

1. Start the Zookeeper and Kafka Docker:

    ```bash
    $ docker run -d -p 2181:2181 --name zookeeper jplock/zookeeper
    $  docker run -d --name kafka --link zookeeper:zookeeper ches/kafka
    ```
    
    To consume messages in the terminal, you can use the Kafka CLI command:
    
    ```bash
    $ ZK_IP=$(docker inspect --format '{{ .NetworkSettings.IPAddress }}' zookeeper)
    $ KAFKA_IP=$(docker inspect --format '{{ .NetworkSettings.IPAddress }}' kafka)
    
    $ docker run --rm ches/kafka kafka-console-consumer.sh --topic test --from-beginning --zookeeper $ZK_IP:2181
    ```
    

2. Build the sample using maven:
   
   ```bash
   $ cd kumuluzee-kafka/kumuluzee-kafka-producer
   $ mvn clean package
   ```

3. Run the sample:

    ```bash
    $ java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```

    in Windows environment use the command
    ```batch
    java -cp target/classes;target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
4. The mesage producing service can be accessed on the following URL:
* JAX-RS REST resource, for producing messages - http://localhost:8080/v1/produce
    with a POST request with a json object, for example:
    
    ```javascript
      {
        "content":"Hello World",
        "key":"1",
        "topic":"test"
      }
    ```

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a service, which uses KumuluzEE Kafka extension.
We will develop a simple REST service for producing Kafka messages:
* POST http://localhost:8080/v1/produce

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet, JAX-RS)
* Add Maven dependency to KumuluzEE Kafka extension
* Implement the service
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

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty`, `kumuluz-jax-rs-jersey` and `kumuluzee-kafka` dependencies:
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
        <groupId>com.kumuluz.ee.kafka</groupId>
        <artifactId>kumuluzee-kafka</artifactId>
        <version>${kumuluzee-kafka.version}</version>
    </dependency>
</dependencies>
```

Add the `maven-dependency-plugin` build plugin to copy all the necessary dependencies into target folder:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <version>2.10</version>
            <executions>
                <execution>
                    <id>copy-dependencies</id>
                    <phase>package</phase>
                    <goals>
                        <goal>copy-dependencies</goal>
                    </goals>
                    <configuration>
                        <includeScope>runtime</includeScope>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Implement the servlet


Register your module as JAX-RS service and define the application path. You could do that in web.xml or
for example with `@ApplicationPath` annotation:

```java
@ApplicationPath("v1")
public class DiscoverApplication extends Application {
}
```

Implement JAX-RS resource, which will use the injected TestProducer class for producing messages:

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("produce")
@RequestScoped
public class ProducerResource {

    @Inject
    private TestProducer producer;

    @POST
    public Response produceMessage(Message msg){

        producer.send(msg.getTopic(), msg.getContent(), msg.getKey());

        return Response.ok().build();

    }
}
```

We use a Message POJO in this example for receiving the message data from the POST request:

```java
public class Message {
    private String key;
    private String content;
    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
```

Implement the Kafka message producing in the TestProducer class:

```java
@ApplicationScoped
public class TestProducer {

    @Inject
    @KafkaProducer
    private Producer producer;

    public void send(String topic, String msg, String key) {
        ProducerRecord<String,String> record = new ProducerRecord<String,String>(topic, key, msg);

        producer.send(record,
                new Callback() {
                    public void onCompletion(RecordMetadata metadata, Exception e) {
                        if(e != null) {
                            e.printStackTrace();
                        } else {
                            System.out.println("The offset of the produced message record is: " + metadata.offset());
                        }
                    }
                });
    }

}
```

In the example above, we inject the Kafka Producer with the `@Inject` and `@KafkaProducer` annotation. From the message data we create 
a ProducerRecord `new ProducerRecord<String,String>(topic, key, msg)` and call the method send on the producer with the ProducerRecord 
and the Callback parameters. In the Callback we receive the sent message metadata `RecordMetadata` or the Exception if an error occured.

### Add required configuration for the Kafka Producer

You have to add the Kafka Producer configuration using any KumuluzEE configuration source.

For example, you can use config.properties file, placed in resources folder:

```properties
producer.bootstrap.servers=172.17.0.3:9092
producer.acks=all
producer.retries=0
producer.batch.size=16384
producer.linger.ms=1
producer.buffer.memory=33554432
producer.key.serializer=org.apache.kafka.common.serialization.StringSerializer
producer.value.serializer=org.apache.kafka.common.serialization.StringSerializer
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
 