# Verticle Discovery &mdash; sample for bridge discovery

> Register a Vert.x service and request a service registered with e.g. KumuluzEE Discovery

The objective of this sample is to get familiar with publishing services on Vert.x Service Discovery and
requesting a service from a microservice using KumuluzEE Reactive Vert.x and KumuluzEE Discovery extensions.
The tutorial will guide you through all the necessary steps.
You will add dependencies into `pom.xml`.
You will develop a simple server Verticle that will publish a service on Vert.x Service Discovery and 
request service url via REST.
Required knowledge: basic familiarity with Vert.x.

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

This sample depends on the following sample:
* [`kumuluzee-reactive-vertx-bridge`](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-reactive-vertx/kumuluzee-reactive-vertx-bridge)
Microservice that acts as a bridge between KumuluzEE Discovery and Vert.x Service Discovery

## Usage

The example uses maven to build a fat jar.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-reactive-vertx/simple-verticles/verticle-discovery
    $ mvn clean package
    ```

2. Run the sample:

    ```batch
    java -jar target\${project.build.finalName}-fat.jar -cluster
    ```
 
The application/service can be accessed on the following URL:
* REST resource - http://localhost:8082/

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a Vert.x discovery Verticle.
* GET http://localhost:8082/ - status of this service
* GET http://localhost:8082/discover - discovered service's url

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies
* Implement discovery Verticle
* Build and run

### Add Maven dependencies

Add `vertx-web` and `vertx-hazelcast` for clustering.

```xml
<dependencies>
    <dependency>
        <groupId>io.vertx</groupId>
        <artifactId>vertx-web</artifactId>
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

### Implement discovery Verticle

Implement a class e.g. DiscoveryVerticle that extends `AbstractVerticle`.

In the example below we will first create a simple server, which will respond to our requests.

```java
HttpServer server = vertx.createHttpServer();
Router router = Router.router(vertx);

router.get("/").handler(routingContext -> {
    routingContext.response()
            .end((new JsonObject().put("status", "UP")).toString());
});

router.get("/discover").handler(routingContext -> {
    getService(routingContext.response());
});

server.requestHandler(router::accept).listen(8082);
```

Then we will publish the created endpoint on Vert.x Service Discovery.

```java
discovery = ServiceDiscovery.create(vertx);

record = HttpEndpoint.createRecord("some-rest-api", "localhost", 8080, "");

discovery.publish(record, ar -> {
    if (ar.succeeded()) {
        record.setRegistration(ar.result().getRegistration());
        log.info("Service was successfully registered.");
    } else {
        log.info("Vert.x service registration failed.");
    }
});
```

Finally we will create a method for requesting a service which will be called once we hit `http://localhost:8082/discover` endpoint.
In this sample we will request for a service `customers-service` with version `1.0.0` located in `dev`
environment.

```java
JsonObject service = new JsonObject().put("name", "customer-service")
    .put("version", "1.0.0")
    .put("env", "dev");
 
vertx.eventBus().send("vertx.discovery.request", service, ar -> {
    if (ar.succeeded()) {
        JsonObject reply = (JsonObject) ar.result().body();
        response.end(reply.toString());
    } else {
        response.end((new JsonObject().put("message", "Failed to retrieve service url.")).toString());
    }
});
```

Don't forget to deregister the service when shutting down the service.

```java
discovery.unpublish(record.getRegistration(), ar -> {
    if(ar.succeeded()) {
        log.info("Service was successfully deregistered.");
    } else {
        log.info("Error deregistering service.");
    }
});
```

### Build and run it

To build and run the example, use the commands as described in previous sections.