# KumuluzEE Kafka &mdash; consume Kafka messages

> Develop a REST service that produces Kafka messages to selected topic

The objective of this sample is to show how to consume Kafka messages.
The tutorial will guide you through all the necessary steps. You will add KumuluzEE dependencies into pom
.xml. You will develop a simple annotated method, which uses KumuluzEE Kafka extension for consuming messages.
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
$ docker run -d -p 2181:2181 --name zookeeper jplock/zookeeper
$ docker run -d --name kafka --link zookeeper:zookeeper ches/kafka
```
   
## Usage

The example uses Docker to set up the Kafka and Zookeeper instances and maven to build and run the microservice.

1. Start the Zookeeper and Kafka Docker:

    ```bash
    $ docker run -d -p 2181:2181 --name zookeeper jplock/zookeeper
    $ docker run -d --name kafka --link zookeeper:zookeeper ches/kafka
    ```
    
    To produce messages in the terminal, you can use the Kafka CLI command:
    
    ```bash
    $ ZK_IP=$(docker inspect --format '{{ .NetworkSettings.IPAddress }}' zookeeper)
    $ KAFKA_IP=$(docker inspect --format '{{ .NetworkSettings.IPAddress }}' kafka)
    
    $ docker run --rm --interactive ches/kafka kafka-console-producer.sh --topic test --broker-list $KAFKA_IP:9092
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

This tutorial will guide you through the steps required to create a Kafka Consumer with the help of the KumuluzEE Kafka extension.
We will develop a simple annotated method which will be invoked when the message is consumed.

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet, JAX-RS)
* Add Maven dependency to KumuluzEE Kafka extension
* Implement the annotated method
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

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-kafka` dependencies:
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
        <artifactId>kumuluzee-cdi-weld</artifactId>
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

### Implement the onMessage method

Implement class for example TestConsumer with a method annotated with `@KafkaListener(topics = {"test"})`. 
The method takes for a parameter the `ConsumerRecord` that contains the data of the received message.

```java
public class TestConsumer {

    private static final Logger log = Logger.getLogger(TestConsumer.class.getName());

    @KafkaListener(topics = {"test"})
    public void onMessage(ConsumerRecord<String, String> record) {

        log.info(String.format("Consumed message: offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value()));

    }
}
```

In the example above, we defined the topics names with the parameter of the `@KafkaListener` annotation, 
but we could also rename the onMessage method to the desired topic name.

### Add required configuration for the Kafka Producer

You have to add the Kafka Consumer configuration using any KumuluzEE configuration source.

For example, you can use config.properties file, placed in resources folder:

```properties
kumuluzee.kafka.consumer.bootstrap.servers=172.17.0.3:9092
kumuluzee.kafka.consumer.group.id=group1
kumuluzee.kafka.consumer.enable.auto.commit=true
kumuluzee.kafka.consumer.auto.commit.interval.ms=1000
kumuluzee.kafka.consumer.auto.offset.reset=earliest
kumuluzee.kafka.consumer.key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
kumuluzee.kafka.consumer.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
 