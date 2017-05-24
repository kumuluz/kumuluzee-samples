# KumuluzEE Discovery -- register service 

> Develop a REST KumuluzEE microservice and add automatic service registration with etcd.

The objective of this sample is to show how to register a REST service with etcd using KumuluzEE service discovery.
The tutorial will guide you through the necessary steps. You will add KumuluzEE dependencies into pom.xml.
You will use existing Jax-Rs sample, described [here](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs).
Required knowledge: basic familiarity with JAX-RS 2 and basic concepts of REST and JSON; basic familarity with etcd.

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

4. etcd:
    * If you have installed etcd, you can check the version by typing the following in a command line:
    
        ```
        etcd --version
        ```

## Prerequisites

This sample does not contain any prerequisites and can be started on its own.

## Usage

The example uses maven to build and run the microservice.

1. Build the sample using maven:

    ```bash
    $ cd discovery-samples/discovery-register
    $ mvn clean package
    ```

2. Start local etcd instance in another terminal:

    ```bash
    $ etcd
    ```

3. Run the sample:

    ```bash
    $ PORT=8081 java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```

    in Windows environment, set environment variable PORT to 8081 and use the command
    ```batch
    java -cp target/classes;target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
    Port 8081 is used because we want to run another microservice, which discovers this service.
    
The application/service can be accessed on the following URL:
* JAX-RS REST resource - http://localhost:8081/v1/customers

The application is registered with etcd. You can discover it using one of the discover samples:
* [discover-servlet sample](http://TODO.url)
* [discover-jaxrs sample](http://TODO.url)

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to register KumuluzEE microservice with etcd. 
We will use existing [sample Customer REST service](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) with the following resources:
* GET http://localhost:8080/v1/customers - list of all customers 
* GET http://localhost:8080/v1/customers/{customerId} – details of customer with ID {customerId}
* POST http://localhost:8080/v1/customers – add a customer
* DELETE http://localhost:8080/v1/customers/{customerId} – delete customer with ID {customerId}

We will follow these steps:
* Import a Maven sample, mentioned above, in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE service discovery
* Annotate JAX-RS Application class with RegisterService annotation
* Build the microservice
* Run it

### Add Maven dependencies

Add the `kumuluzee-discovery-etcd` dependency to the sample:
```xml
<dependencies>
    ...
    
    <dependency>
        <groupId>com.kumuluz.ee.discovery</groupId>
        <artifactId>kumuluzee-discovery-etcd</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### Annotate JAX-RS Application

Add `@RegisterService` annotation to JAX-RS Application class (CustomerApplication.java):

```java
@RegisterService(value = "customer-service")
@ApplicationPath("v1")
public class CustomerApplication extends Application {
}
```

### Add required configuration for the service discovery

You can add configuration using any KumuluzEE configuration source.

For example, you can use config.yml file, placed in resources folder:
```yaml
kumuluzee:
  env: dev
  version: 1.0.0
  base-url: http://localhost:8081
  discovery:
    etcd:
      hosts: http://127.0.0.1:2379
    ttl: 10
    ping-interval: 5
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.