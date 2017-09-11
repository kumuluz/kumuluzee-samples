# KumuluzEE Event Streaming Kafka &mdash; produce Kafka messages

> Develop a REST service that produces Kafka messages to selected topic

The objective of this sample is to show how to produce Kafka messages with a simple Rest service using KumuluzEE Event Streming extension. The tutorial will guide you through all the necessary steps. You will add KumuluzEE dependencies into pom.xml. You will develop a simple REST service, which uses KumuluzEE Event Streaming Kafka extension for producing messages.
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

To run this sample you will need an Kafka and Zookeeper instance [Kafka Quickstart](https://kafka.apache.org/quickstart).
There are a lot of Kafka Docker available on the Docker hub, in this tutorial we use  
[ches/kafka](https://hub.docker.com/r/ches/kafka/) and a separate Docker with the Zookeeper instance [jplock/zookeeper](https://hub.docker.com/r/jplock/zookeeper/)
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
    
    To consume messages in the terminal, you can use the Kafka CLI command:
    
    ```bash
    $ docker run --rm --network kafka-net ches/kafka \
      kafka-console-consumer.sh --topic test --from-beginning --bootstrap-server kafka:9092
    ```
    

2. Build the sample using maven:
   
   ```bash
   $ cd kumuluzee-kafka/kumuluzee-kafka-producer
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

This tutorial will guide you through the steps required to create a service, which uses KumuluzEE Event Streaming Kafka extension.
We will develop a simple REST service for producing Kafka messages:
* POST http://localhost:8080/v1/produce

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components with the microProfile-1.0 dependency
* Add Maven dependency to KumuluzEE Event Streaming Kafka extension
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

Add the `kumuluzee-microProfile-1.0` and `kumuluzee-streaming-kafka` dependencies:
```xml
<dependencies>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
        <artifactId>kumuluzee-microProfile-1.0</artifactId>
        <version>2.2.0</version>
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
Add the `kumuluzee-maven-plugin` build plugin to package microservice as uber-jar:

```xml
<build>
    <plugins>
        <plugin>
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
        </plugin>
    </plugins>
</build>
```

### Implement the servlet

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

Register your module as JAX-RS service and define the application path. You could do that in web.xml or
for example with `@ApplicationPath` annotation:

```java
@ApplicationPath("v1")
public class ProducerApplication extends Application {
}
```

Implement JAX-RS resource, with a POST method for producing messages. Inject the Kafka Producer with the `@StreamProducer` 
annotation and implement the producing of Kafka messages:

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/produce")
@RequestScoped
public class ProducerResource {

    @Inject
    @StreamProducer
    private KafkaStreamProducer producer;

    @POST
    public Response produceMessage(Message msg){

        ProducerRecord<String,String> record = new ProducerRecord<String,String>( msg.getTopic(), msg.getKey(), msg.getContent());

        producer.send(record,
                (metadata, e) -> {
                    if(e != null) {
                        e.printStackTrace();
                    } else {
                        System.out.println("The offset of the produced message record is: " + metadata.offset());
                    }
                });

        return Response.ok().build();

    }
}
```

In the example above, we inject the Kafka Producer with the `@Inject` and `@StreamProducer` annotation. From the message data we create 
a ProducerRecord `new ProducerRecord<String,String>(topic, key, msg)` and call the method send on the producer with the ProducerRecord 
and the Callback parameters. In the Callback we receive the sent message metadata `RecordMetadata` or the Exception if an error occurred.

### Add required configuration for the Kafka Producer

You have to add the Kafka Producer configuration using any KumuluzEE configuration source.

For example, you can use config.properties file, placed in resources folder:

```yaml
kumuluzee:
  streaming:
    kafka:
      producer:
        bootstrap-servers: localhost:9092
        acks: all
        retries: 0
        batch-size: 16384
        linger-ms: 1
        buffer-memory: 33554432
        key-serializer: org.apache.kafka.common.serialization.StringSerializer
        value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.

