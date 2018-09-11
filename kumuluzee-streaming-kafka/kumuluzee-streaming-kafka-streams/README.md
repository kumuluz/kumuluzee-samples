# KumuluzEE Event Streaming Kafka &mdash; Stream processing with Kafka Streams

> Develop a simple WordCount stream processing application using KumuluzEE Event Streaming with Kafka Streams

The objective of this sample is to show how to easily develop a stream processing application using 
KumuluzEE Event Streaming with Kafka Streams. The tutorial will guide you through all the necessary steps. 
You will add KumuluzEE dependencies into pom.xml. You will develop a simple stream processing application,
that implements the WordCount algorithm, which computes a word occurrence histogram from the input text.
Required knowledge: basic familiarity with Apache Kafka Streams.

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
[ches/kafka](https://hub.docker.com/r/ches/kafka/) and a separate Docker with the Zookeeper instance 
[jplock/zookeeper](https://hub.docker.com/r/jplock/zookeeper/)
Here is an example on how to quickly run the Zookeeper and Kafka Docker:

```bash
$ docker network create kafka-net
  
$ docker run -d -p 2181:2181 --name zookeeper --network kafka-net zookeeper:3.4
$ docker run -d -p 9092:9092 --name kafka --network kafka-net --env ZOOKEEPER_IP=zookeeper --env KAFKA_ADVERTISED_HOST_NAME={docker_host_ip} ches/kafka
```

Replace `{docker_host_ip}` with you Docker host IP.

## Usage

The example uses Docker to set up the Kafka and Zookeeper instances and maven to build and run the microservice.

1. Start the Zookeeper and Kafka Docker:

    ```bash
    $ docker network create kafka-net
      
    $ docker run -d -p 2181:2181 --name zookeeper --network kafka-net zookeeper:3.4
    $ docker run -d -p 9092:9092 --name kafka --network kafka-net --env ZOOKEEPER_IP=zookeeper --env KAFKA_ADVERTISED_HOST_NAME={docker_host_ip} ches/kafka
    ```
    
    Replace `{docker_host_ip}` with you Docker host IP.
    
    To consume messages in the terminal, you can use the Kafka CLI command:
    
    ```bash
    $ docker run --rm --network kafka-net ches/kafka \
      kafka-console-consumer.sh --topic test --from-beginning --bootstrap-server kafka:9092
    ```
    
    To produce messages in the terminal, you can use the Kafka CLI command:
        
    ```bash
    $ docker run --rm --interactive --network kafka-net ches/kafka \
      kafka-console-producer.sh --topic test --broker-list kafka:9092
    <type some messages followed by newline>
    ```
    

2. Build the sample using maven:
   
   ```bash
   $ cd kumuluzee-streaming-kafka/kumuluzee-kafka-streams
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

To shut down the example simply stop the processes in the foreground and all Docker containers started for this example.

## Tutorial

This tutorial will guide you through the steps required to create a service, 
which uses KumuluzEE Event Streaming Kafka extension.
We will develop a simple stream processing application.

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
            <version>${kumuluzee.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Add the `kumuluzee-microProfile-1.0`, `kumuluzee-streaming-kafka` and `kafka-streams` dependencies:
```xml
<dependencies>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
        <artifactId>kumuluzee-microProfile-1.0</artifactId>
        <version>${kumuluzee.version}</version>
    </dependency>
    <dependency>
        <groupId>com.kumuluz.ee.streaming</groupId>
        <artifactId>kumuluzee-streaming-kafka</artifactId>
        <version>${kumuluzee-streaming-kafka.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.kafka</groupId>
        <artifactId>kafka-streams</artifactId>
        <version>${kafka.version}</version>
    </dependency>
</dependencies>
```

We will use `kumuluzee-logs` for logging in this sample, so you need to include kumuluzee logs implementation dependency:
```xml
<dependency>
    <artifactId>kumuluzee-logs-log4j2</artifactId>
    <groupId>com.kumuluz.ee.logs</groupId>
    <version>${kumuluzee-logs.version}</version>
</dependency>
```

For more information about the KumuluzEE Logs visit the [KumuluzEE Logs Github page](https://github.com/kumuluz/kumuluzee-logs).
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
    <version>${log4j-slf4j.version}</version>
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

### Implement the stream processor

In an ApplicationScoped class we create a StreamProcessor annotated method that returns the StreamsBuilder object, the
annotation has the parameter `id` set to "word-count" and autoStart to `false`, the configuration prefix has the default
value "streams".

In the annotated method we:
1. Construct the StreamsBuilder.
2. Construct a `KStream` from the input topic "input", where message values represent lines of text (for the sake of 
this example, we ignore whatever may be stored in the message keys).
3. With the help of Kafka `KTable` we then split each text line from the `KStream`, by whitespace, into words. Group the 
text words as message keys and count the occurrences of each word (message key). 
4. Then we store the running counts as a changelog stream to the output topic "output".
5. At the end we return the constructed StreamsBuilder.

```java
@ApplicationScoped
public class WordCountStreamsBuilder {

    @StreamProcessor(id = "word-count", autoStart = false)
    public StreamsBuilder wordCountBuilder() {

        StreamsBuilder builder = new StreamsBuilder();

        // Serializers/deserializers (serde) for String and Long types
        final Serde<String> stringSerde = Serdes.String();

        // Construct a `KStream` from the input topic "streams-plaintext-input", where message values
        // represent lines of text (for the sake of this example, we ignore whatever may be stored
        // in the message keys).
        KStream<String, String> textLines = builder.stream("in",
                Consumed.with(stringSerde, stringSerde));

        KTable<String, String> wordCounts = textLines
                // Split each text line, by whitespace, into words.
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                // Group the text words as message keys
                .groupBy((key, value) -> value)
                // Count the occurrences of each word (message key).
                .count()
                .mapValues((key, value) -> value.toString());

        // Store the running counts as a changelog stream to the output topic.
        wordCounts.toStream().to("out", Produced.with(stringSerde, stringSerde));

        return builder;

    }
}
```

Since we set the StreamProcessor parameter autoStart to `false` we must manually start the Streams instance. We can do 
this by injecting StreamsController annotated with `@StreamProcessorController` with the parameter `id` set to the id of
the previously annotated `wordCountBuilder` method.
With the `StreamsController` class we can access the KafkaStreams methods that manage the lifecycle of the created 
Kafka Streams instance.

In the example below we can see that we created a method `startStream` that observes the initialization of the 
ApplicationScoped class. In the method we start the Kafka Streams instance and attach a shutdown handler to catch 
control-c and close the stream processor.

```java
@ApplicationScoped
public class WordCountStreamsControl {

    @StreamProcessorController(id="word-count")
    StreamsController wordCountStreams;

    public void startStream(@Observes @Initialized(ApplicationScoped.class) Object init) {
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-wordcount-shutdown-hook") {
            @Override
            public void run() {
                wordCountStreams.close();
                latch.countDown();
            }
        });

        try {
            wordCountStreams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
```

### Add required configuration for the Kafka Producer

You have to add the Kafka Streams configuration using any KumuluzEE configuration source.

For example, you can use config.yaml file, placed in resources folder:

```yaml
kumuluzee:
  streaming:
    kafka:
      streams:
        bootstrap-servers: localhost:9092
        application-id: sample-word-count
        default-key-serde: org.apache.kafka.common.serialization.Serdes$StringSerde
        default-value-serde: org.apache.kafka.common.serialization.Serdes$StringSerde
        commit-interval-ms: 500
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.

