# Microservices with KumuluzEE MicroProfile 1.2

> Build a microservice with Eclipse MicroProfile APIs implemented in KumuluzEE MicroProfile 1.2

The objective of this sample is to demonstrate how to use KumuluzEE MicroProfile 1.2 to build a microservice 
with [Eclipse MicroProfile](http://microprofile.io/) APIs, including health checks, configuration management, 
metrics collection, fault tolerance and JWT authentication mechanisms.

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
    $ cd kumuluzee-microProfile-1.2
    $ mvn clean package
    ```

2. Run the sample:
* Uber-jar:

    ```bash
    $ java -jar target/${project.build.finalName}.jar
    ```
    
    in Windows environemnt use the command
    ```batch
    java -jar target\${project.build.finalName}.jar
    ```

* Exploded:

    ```bash
    $ java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
    in Windows environment use the command
    ```batch
    java -cp 'target/classes;target/dependency/*' com.kumuluz.ee.EeApplication
    ```
    
    
The microservice can be accessed on the following URL:
* JAX-RS REST resource - http://localhost:8080/v1/customers
* Metrics JSON & Prometheus - http://localhost:8080/metrics
* Health status - http://localhost:8080/health

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to use KumuluzEE MicroProfile 1.2 to develop a cloud-native 
microservice with Eclipse MicroProfile APIs. We will extend the existing
[KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs).
First, complete the existing JAX-RS sample tutorial, or clone the JAX-RS sample code.

We will follow these steps:
* Complete the tutorial for
[KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) or clone the existing
sample
* Change Maven dependencies
* Add health check and configuration
* Add metrics collectors 
* Add fault tolerance mechanisms
* Access the JWT security token 
* Build the microservice and run it

### Change Maven dependencies

Since your existing starting point is the existing KumuluzEE JAX-RS REST sample, you should already have the
dependencies for `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-jax-rs-jersey` configured in `pom.xml`. 
Replace them with `kumuluzee-microProfile-1.2` which already includes all of them, together with KumuluzEE CDI 
component, KumuluzEE Health, KumuluzEE Config MicroProfile, KumuluzEE Metrics, KumuluzEE Fault Tolerance and 
KumuluzEE JWT Authentication. 

```xml
<dependencies>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
        <artifactId>kumuluzee-microProfile-1.2</artifactId>
    </dependency>
</dependencies>
```

### Add health check and configuration

We will implement a custom health check, using the Eclipse MicroProfile Health Check API. The goal of this health 
check is to demonstrate the implementation and usage of custom health checks. Our health check will determine the 
health of the microservice according to the configuration value `healthy` that will be retrieved with the Eclipse 
MicroProfile Config API. We implement our health check as a CDI bean that extends the `HealthCheck` interface. We 
annotate it with `@Health` annotation and implement the `call` method.

```java
@Health
@ApplicationScoped
public class ServiceHealthCheck implements HealthCheck {

    @Inject
    @ConfigProperty(name = "healthy")
    private Provider<Boolean> isHealthy;

    @Override
    public HealthCheckResponse call() {

        if (isHealthy.get().booleanValue()) {
            return HealthCheckResponse.named(ServiceHealthCheck.class.getSimpleName()).up().build();
        } else {
            return HealthCheckResponse.named(ServiceHealthCheck.class.getSimpleName()).down().build();
        }

    }
}
```

Configuration value `healthy` is read from the configuration file `config.yaml` using the MicroProfile annotation 
`@ConfigProperty`:

```yalm
healthy: true
```

We can access the health status of our microservice on the endpoint `http://localhost:8080/health` which should 
return HTTP status `200` with outcome `UP`. If we change the value in the configuration file and restart the 
microservice, health endpoint will return HTTP status `503` with outcome `DOWN`.

More information about [KumuluzEE Config MicroProfile](https://github.com/kumuluz/kumuluzee-config-mp) and 
[KumuluzEE Health](https://github.com/kumuluz/kumuluzee-health) can be found on GitHub. 

### Add metrics collectors 

In this step we will use Eclipse MicroProfile Metrics API to equip or microservice with a few metrics collectors. We 
will use annotations `@Timed`, `@Metered` and `@Gauge` to collect metrics on selected methods in `CustomerResource` class.

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
@RequestScoped
public class CustomerResource {

    @GET
    @Path("add-sample-names")
    @Timed(name = "add-sample-names-timer")
    public Response addSampleNames() {
        addNewCustomer(new Customer(Database.getCustomers().size(), "Daniel", "Ornelas"));
        addNewCustomer(new Customer(Database.getCustomers().size(), "Dennis", "McBride"));
        addNewCustomer(new Customer(Database.getCustomers().size(), "Walter", "Wright"));
        addNewCustomer(new Customer(Database.getCustomers().size(), "Mitchell", "Kish"));
        addNewCustomer(new Customer(Database.getCustomers().size(), "Tracy", "Edwards"));

        return Response.noContent().build();
    }
    
    @DELETE
    @Path("{customerId}")
    @Metered(name = "customer_deleting_meter")
    public Response deleteCustomer(@PathParam("customerId") int customerId) {
        Database.deleteCustomer(customerId);
        return Response.noContent().build();
    }

    @Gauge(name = "customer_count_gauge", unit = MetricUnits.NONE)
    private int getCustomerCount() {
        return Database.getCustomers().size();
    }
}
```

Now we will add web instrumentation that monitors requests and responses at a certain endpoint. We will configure it to 
monitor the endpoint `/v1/customers`. We do that by simply adding the following lines to the configuration file 
`config.yaml`:

```yaml
kumuluzee:
    metrics:
        webinstrumentation:
          - name: customersEndpoint
            url-pattern: /v1/customers/*
```

Collected metrics can be accessed at the endpoint `http://localhost:8080/metrics`, which by default returns data in 
Prometheus format. To view metrics data as a JSON object, add request header `Accept` and set it to `application/json`.

More information about KumuluzEE Metrics can be found in the 
[GitHub repository](https://github.com/kumuluz/kumuluzee-metrics).


### Add fault tolerance mechanisms

In this step we will use Eclipse MicroProfile Fault Tolerance API to enable fault tolerance mechanisms. We add 
annotations `@GroupKey`, `@CommandKey`, `@Timeout` and `@CircuitBreaker` to enable circuit breaker on the 
`addNewCustomer` method.

More information about KumuluzEE Fault Tolerance can be found in the 
[GitHub repository](https://github.com/kumuluz/kumuluzee-fault-tolerance). 

### Access the JWT security token 

In this step we will use Eclipse MicroProfile JWT Authentication API to access the JWT token.
 
In order for the extension to work correctly, we must first provide two configuration properties:

```yaml
kumuluzee:
  jwt-auth:
    public-key: MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnOTgnGBISzm3pKuG8QXMVm6eEuTZx8Wqc8D9gy7vArzyE5QC/bVJNFwlz...
    issuer: http://example.org/auth
```

JWT token can then be injected as a MicroProfile `JsonWebToken` object with the following code:

```java
@Inject
private JsonWebToken principal; 
```

More information about KumuluzEE JWT Authentication can be found in the 
[GitHub repository](https://github.com/kumuluz/kumuluzee-jwt-auth). 

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
