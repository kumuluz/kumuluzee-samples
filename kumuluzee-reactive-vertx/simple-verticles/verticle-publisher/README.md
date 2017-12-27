# Verticle Publisher &mdash; publish on Vert.x event bus

> Publish events on Vert.x event bus using a Verticle

The objective of this sample is to publish events on Vert.x event bus which will be captured with a microservice using KumuluzEE Reactive Vert.x extension. The tutorial will guide you through all the necessary steps. You will add dependencies into `pom.xml`. You will develop a simple Verticle that will publish on Vert.x event bus. Required knowledge: basic familiarity with Vert.x.

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

This sample does not contain any prerequisites and can be started on its own.

## Usage

The example uses maven to build a fat jar.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-reactive-vertx/simple-verticles/verticle-publisher
    $ mvn clean package
    ```

2. Run the sample:

    ```batch
    java -jar target\${project.build.finalName}-fat.jar -cluster
    ```
 
3. Message will be printed out in the terminal when the Verticle receives it.

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a Vert.x event bus publisher Verticle.

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies
* Implement publisher Verticle
* Build and run

### Add Maven dependencies

Add `vertx-core` and `vertx-hazelcast` for clustering.

```xml
<dependencies>
    <dependency>
        <groupId>io.vertx</groupId>
        <artifactId>vertx-core</artifactId>
        <version>${vertx.version}</version>
    </dependency>
    <dependency>
        <groupId>io.vertx</groupId>
        <artifactId>vertx-hazelcast</artifactId>
        <version>${vertx.version}</version>
    </dependency>
</dependencies>
```

Add the `maven-compiler-plugin` build plugin to compile, `maven-shade-plugin` to package and `exec-maven-plugin` to run the project.

### Implement publisher Verticle

Implement a class e.g. PublisherVerticle that extends `AbstractVerticle`.

In the example below we get a reference to the Vert.x event bus and start publishing messages on the given address `tacos` every 1000ms (1 second).

```java
import io.vertx.core.AbstractVerticle;

public class PublisherVerticle extends AbstractVerticle {

	@Override
	public void start() {
		vertx.setPeriodic(1000, id -> {
			vertx.eventBus().send("tacos", "Hello from Vert.x.");
		});
	}

}
```

### Build and run it

To build and run the example, use the commands as described in previous sections.