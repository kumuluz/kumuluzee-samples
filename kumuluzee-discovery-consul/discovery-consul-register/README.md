# KumuluzEE Discovery &mdash; register service with Consul

> Develop a REST KumuluzEE microservice and register it with Consul.

The objective of this sample is to show how to register a REST service with Consul using KumuluzEE Discovery extension.
This tutorial will guide you through all the necessary steps. You will add KumuluzEE dependencies into pom.xml.
You will use existing JAX-RS sample, described [here](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs).
Required knowledge: basic familiarity with JAX-RS and basic concepts of REST and JSON; basic familiarity with Consul.

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

To run this sample you will need a Consul instance. Note that such setup with only one node is not viable for 
production environments, but only for developing purposes. Download Consul and run it in development mode with the 
following command:

   ```bash
    $ consul agent -dev
   ```

## Usage

The example uses maven to build and run the microservice.

1. Build the sample using maven:

    ```bash
    $ cd discovery-samples/discovery-register
    $ mvn clean package
    ```

2. Start local Consul instance:

3. Run the sample:

    ```bash
    $ PORT=8081 java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```

    in Windows environment use the command
    ```batch
    $env:PORT=8087;java -cp target/classes;target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
    Port 8081 is used because we want to run another microservice, which discovers this service.
    
The application/service can be accessed on the following URL:
* JAX-RS REST resource - http://localhost:8081/v1/customers

The application is registered with Consul. You can discover it using one of the discover samples:
* [discover-consul-servlet sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery-consul/discovery-consul-discover-servlet)
* [discover-consul-jaxrs sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery-consul/discovery-consul-discover-jaxrs)

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to register KumuluzEE microservice with Consul. 
We will use existing [sample Customer REST service](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) with the following resources:
* GET http://localhost:8080/v1/customers - list of all customers 
* GET http://localhost:8080/v1/customers/{customerId} – details of customer with ID {customerId}
* POST http://localhost:8080/v1/customers – add a customer
* DELETE http://localhost:8080/v1/customers/{customerId} – delete customer with ID {customerId}

We will follow these steps:
* Import a Maven sample, mentioned above, in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependency to KumuluzEE Discovery extension
* Annotate JAX-RS Application class with @RegisterService annotation
* Build the microservice
* Run it

### Add Maven dependencies

Add the `kumuluzee-discovery-consul` dependency to the sample:
```xml
<dependencies>
    ...
    
    <dependency>
        <groupId>com.kumuluz.ee.discovery</groupId>
        <artifactId>kumuluzee-discovery-consul</artifactId>
        <version>${kumuluzee-discovery.version}</version>
    </dependency>
</dependencies>
```

### Annotate JAX-RS Application

Add the `@RegisterService` annotation to JAX-RS Application class (CustomerApplication.java):

```java
@RegisterService
@ApplicationPath("v1")
public class CustomerApplication extends Application {
}
```

### Add required configuration for the service discovery

You can add configuration using any KumuluzEE configuration source.

For example, you can use config.yml file, placed in resources folder:
```yaml
kumuluzee:
  name: customer-service
  env:
    name: dev
  version: 1.0.0
  discovery:
    ttl: 20
    ping-interval: 15
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.