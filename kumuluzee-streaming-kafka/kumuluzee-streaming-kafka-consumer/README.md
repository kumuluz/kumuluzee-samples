# KumuluzEE Event Streaming with Kafka &mdash; consume Kafka messages

> Develop a REST service that consumes Kafka messages of selected topic

The objective of this sample is to show how to consume Kafka messages using KumuluzEE Event Streaming extension.
The tutorial will guide you through all the necessary steps. You will add KumuluzEE dependencies into pom.xml. You will develop a simple annotated method, which uses the KumuluzEE Event Streaming Kafka extension for consuming messages.
Required knowledge: basic familiarity with Apache Kafka.

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

To run this sample you will need a Kafka and Zookeeper instance [Kafka Quickstart](https://kafka.apache.org/quickstart). 
There are a lot of Kafka Docker available on the Docker hub, in this tutorial we use [ches/kafka](https://hub.docker.com/r/ches/kafka/) 
and a separate Docker with the Zookeeper instance [jplock/zookeeper](https://hub.docker.com/r/jplock/zookeeper/)
Here is an example on how to quickly run the Zookeeper and Kafka Docker:

```bash
$ docker network create kafka-net
          
$ docker run -d -p 2181:2181 --name zookeeper --network kafka-net zookeeper:3.4
$ docker run -d -p 9092:9092 --name kafka --network kafka-net --env ZOOKEEPER_IP=zookeeper --env KAFKA_ADVERTISED_HOST_NAME={docker_host_ip} ches/kafka
```
   
## Usage

The example uses Docker to set up the Kafka and Zookeeper instances and maven to build and run the microservice.

1. Start the Zookeeper and Kafka Docker:

    ```bash
    $ docker network create kafka-net
          
    $ docker run -d -p 2181:2181 --name zookeeper --network kafka-net zookeeper:3.4
    $ docker run -d -p 9092:9092 --name kafka --network kafka-net --env ZOOKEEPER_IP=zookeeper --env KAFKA_ADVERTISED_HOST_NAME={docker_host_ip} ches/kafka
    ```
    
    To produce messages in the terminal, you can use the Kafka CLI command:
    
    ```bash
    $ docker run --rm --interactive --network kafka-net ches/kafka \
      kafka-console-producer.sh --topic test --broker-list kafka:9092
    <type some messages followed by newline>
    ```
    

2. Build the sample using maven:
   
   ```bash
   $ cd kumuluzee-kafka/kumuluzee-kafka-consumer
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
    
4. The consumed messages will be printed in the terminal.

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a Kafka Consumer with the help of the KumuluzEE Event Streaming Kafka extension.
We will develop a simple annotated method which will be invoked when the message is consumed. We will also build a Rest service that will display the last 5 received messages:
* GET http://localhost:8080/v1/consume

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components with the microProfile-1.0 dependency
* Add Maven dependency to KumuluzEE Event Streaming Kafka extension
* Implement the annotated method and Rest service
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

Add the `kumuluzee-microProfile-1.0` and `kumuluzee-streaming-kafka` dependencies:
```xml
<dependencies>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
        <artifactId>kumuluzee-microProfile-1.0</artifactId>
    </dependency>
    <dependency>
        <groupId>com.kumuluz.ee.streaming</groupId>
        <artifactId>kumuluzee-streaming-kafka</artifactId>
        <version>${kumuluzee-streaming-kafka.version}</version>
    </dependency>
</dependencies>
```

We will use `kumuluzee-logs` for logging in this sample, so you need to include kumuluzee logs implementation dependency:
```xml
<dependency>
    <artifactId>kumuluzee-logs-log4j2</artifactId>
    <groupId>com.kumuluz.ee.logs</groupId>
    <version>1.1.0</version>
</dependency>
```

For more information about the KumuluzEE Logs visit the [KumuluzEE Logs Github page](https://github.com/kumuluz/kumuluzee-logs). \
Currently, Log4j2 is supported implementation of `kumuluzee-logs`, so you need to include a sample Log4j2 configuration, 
which should be in a file named `log4j2.xml` and located in `src/main/resources`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration name="config-name">
    <Appenders>
        <Console name="console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d %p %marker %m %X %ex %n"/>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="console"/>
        </Root>
    </Loggers>
</Configuration>
```

If you would like to collect Kafka related logs through the KumuluzEE Logs, you have to include the following `slf4j` implementation as dependency:
```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
    <version>2.8.1</version>
</dependency>
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

### Implement the onMessage method and Rest service

Register your module as JAX-RS service and define the application path. You could do that in web.xml or for example with `@ApplicationPath` annotation:

```java
@ApplicationPath("v1")
public class ConsumerApplication extends Application {
}
```

Implement class for example TestConsumer with a method annotated with `@StreamListener(topics = {"test"})`. 
The method takes for a parameter the `ConsumerRecord` that contains the data of the received message.
We will store the received messages in a List. We also implemented a method `getLast5Messages` for getting the last 5 messages from the List.

```java
public class TestConsumer {

    private static final Logger log = Logger.getLogger(TestConsumer.class.getName());

    private List<String> messages = new ArrayList<>();
    
    @StreamListener(topics = {"test"})
    public void onMessage(ConsumerRecord<String, String> record) {

        log.info(String.format("Consumed message: offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value()));

        messages.add(record.value());
    }
    
    public List<String> getLast5Messages() {
        return messages.subList(messages.size()-5, messages.size());
        }
}
```

In the example above, we defined the topics names with the parameter of the `@StreamListener` annotation, 
but we could also rename the onMessage method to the desired topic name.

If you would like to consume a batch fo messages change the onMessage method like this:

```java
@StreamListener(topics = {"test"})
public void onMessage(List<ConsumerRecord<String, String>> records) {
    
    for (ConsumerRecord<String, String> record : records) {
        log.info(String.format("Consumed message: offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value()));
        messages.add(record.value());
    }
    
}
```

Implement JAX-RS resource, with a GET method for displaying the last 5 received  messages. Inject the `TestConsumer` 
and retrieve the Kafka messages:

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/consume")
@RequestScoped
public class ConsumerResource {

    @Inject
    TestConsumer consumer;

    @GET
    public Response getLast5Messages(){

        return Response.status(200).entity(consumer.getLast5Messages()).build();
    }
}
```

To display the use of Kafka manual message offset committing we will implement another method in the `TestConsumer` class, 
with an additional method parameter `Acknowledgement`, this consumer needs to have different configuration of 
`enable-auto-commit: false`: 

```java
@StreamListener(topics = {"test"}, config = "consumer2")
public void manualCommitMessage(ConsumerRecord<String, String> record, Acknowledgement ack) {

    log.info(String.format("Manual committed message: offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value()));
    ack.acknowledge(); // acknowledges the consumed messages

}
```

### Add required configuration for the Kafka Consumer

You have to add the Kafka Consumer configuration using any KumuluzEE configuration source.

For example, you can use config.properties file, placed in resources folder:

```yaml
kumuluzee:
  streaming:
    kafka:
      consumer:
        bootstrap-servers: localhost:9092
        group-id: group1
        enable-auto-commit: true
        auto-commit-interval-ms: 1000
        auto-offset-reset: earliest
        key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
        value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.

