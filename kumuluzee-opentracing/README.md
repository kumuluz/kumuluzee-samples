# KumuluzEE OpenTracing Sample

> Simple example on how to use KumuluzEE OpenTracing extension.

Example demonstrates how to take advantage of distributed tracing 
in KumuluzEE projects.

## Requirements

In order to run this example you will need the following:

1. Java 8 (or newer), you can use any implementation:
    * If you have installed Java, you can check the version by typing the following in a command line:
        
        ```bash
        $ java -version
        ```

2. Maven 3.2.1 (or newer):
    * If you have installed Maven, you can check the version by typing the following in a command line:
        
        ```bash
        $ mvn -version
        ```
        
3. Docker 1.13.0 (or newer):
    * If you have installed Docker, you can check the version by typing the following in a command line:
    
        ```bash
        $ docker --version
        ```

## Jaeger Tracing

### Prerequisites

Make sure you have Jaeger tracing instance running.

```bash
$ docker run -d -p 5775:5775/udp -p 16686:16686 jaegertracing/all-in-one:latest
```


## Zipkin tracing

### Prerequisites

Make sure you have Zipkin tracing instance running.

```bash
$ docker run -d -p 9411:9411 openzipkin/zipkin
```

By default Jaeger tracing is enabled in both sample microservices. 
To enable Zipkin tracing all you need to do is change dependency in
customers and orders module:

```xml
    <!--Replace kumuluzee-opentracing-jaeger with this dependency.-->
    <dependency>
      <groupId>com.kumuluz.ee.opentracing</groupId>
      <artifactId>kumuluzee-opentracing-zipkin</artifactId> 
      <version>${kumuluzee-opentracing.version}</version>
    </dependency>
```

## Usage

The example uses maven to build and run the microservices.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-opentracing
    $ mvn clean package
    ```
    
2. Run each individual microservice separately (separate terminal):
    
    ```bash
    $ java -jar customers/target/opentracing-customers-1.0.0-SNAPSHOT.jar
    $ java -jar orders/target/opentracing-orders-1.0.0-SNAPSHOT.jar
    ```
    
3. Navigate to <http://localhost:3000/v1/customers/1/orders>

4. View traces in [Jaeger console](http://localhost:16686) or [Zipkin console](http://localhost:9411)