# KumuluzEE simple logging sample

> Build a REST service which utilizes the default logging implementation by java.util.logging to log basic metrics and 
pack it as a KumuluzEE microservice

The objective of this sample is to demonstrate how to use the default logging implementation by java.util.logging to 
log basic metrics.

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

The example uses maven to build and run the microservices.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-logs-simple
    $ mvn clean package
    ```

2. Run the sample:

    ```bash
    $ java -Djava.util.logging.config.file=<path>/kumuluzee-samples/kumuluzee-logs-simple/src/main/resources/logging.properties -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
    in Windows environment use the command
    ```batch
    java -Djava.util.logging.config.file=<path>\kumuluzee-samples\kumuluzee-logs-simple\src\main\resources\logging.properties -cp target/classes;target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
    and replace the `<path>` with appropriate directory path.
    
The application/service can be accessed on the following URL:
* JAX-RS REST resource - http://localhost:8080/v1/customers

To shut down the example simply stop the processes in the foreground.

## Tutorial
This tutorial will guide you through the steps required to use KumuluzEE Logs and pack the application as a KumuluzEE microservice. We will extend the existing [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs).
Therefore, first complete the existing JAX-RS sample tutorial, or clone the JAX-RS sample code.

We will follow these steps:
* Complete the tutorial for [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) or clone the existing sample
* Add Maven dependencies
* Add KumuluzEE Logs support
* Configure java.util.logging configuration
* Build the microservice
* Run it

### Add Maven dependencies

Since your existing starting point is the existing KumuluzEE JAX-RS REST sample, you should already have the dependencies for `kumuluzee-bom`, `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-jax-rs-jersey` configured in `pom.xml`.

Add the `kumuluzee-cdi-weld` dependency:
```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-cdi-weld</artifactId>
</dependency>
```

### Add KumuluzEE Logs support

Enhance `CustomerResource` class by adding KumuluzEE Logs annotations:

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
@Log(LogParams.METRICS)
public class CustomerResource {

    ...

    @POST
    @Log(value = LogParams.METRICS, methodCall = false)
    public Response addNewCustomer(Customer customer) {
        Database.addCustomer(customer);
        return Response.noContent().build();
    }
}
```

### Configure java.util.logging configuration

The java.util.logging can be configured by changing the JRE logging configuration file located in 
**JRE_DIRECTORY/lib/logging.properties** or by providing the location of the custom configuration file with system 
property **-Djava.util.logging.config.file**.

In this sample in directory `resources` create file `logging.properties`: 

```
# Default global logging level
.level=ALL

# ConsoleHandler definition
handlers=java.util.logging.ConsoleHandler

# ConsoleHandler configuration settings
java.util.logging.ConsoleHandler.level=ALL
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.