# KumuluzEE Health sample

> Build a REST service which utilizes a built-in health framework to expose a health check and pack it as a KumuluzEE 
microservice

The objective of this sample is to demonstrate how to use the built-in health framework to expose basic health checks.

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

In order to run this sample you will have to setup a local PostgreSQL database:
- __database host__: localhost:5432
- __database name__: customers
- __user__: postgres
- __password__: postgres

The required tables will be created automatically upon running the sample.

## Usage

The example uses maven to build and run the microservices.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-health
    $ mvn clean package
    ```

2. Run the sample:
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
    
The application/service can be accessed on the following URL:
* JAX-RS REST resource - http://localhost:8080/v1/customers
* Health servlet - http://localhost:8080/health

To shut down the example simply stop the processes in the foreground.

## Tutorial
This tutorial will guide you through the steps required to use KumuluzEE Health and pack the application as a KumuluzEE 
microservice. We will extend the existing [KumuluzEE JPA and CDI sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jpa). 
Therefore, first complete the existing KumuluzEE JPA and CDI sample tutorial, or clone the KumuluzEE JPA and CDI sample code.

We will follow these steps:
* Complete the tutorial for [KumuluzEE JPA and CDI sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jpa) or clone the existing sample
* Add Maven dependency
* Implement Health Check Bean
* Add Health configuration
* Build the microservice
* Run it

### Add Maven dependency

Since your existing starting point is the existing KumuluzEE JPA and CDI sample, you should already have the dependencies 
for `kumuluzee-bom`, `kumuluzee-core`, `kumuluzee-servlet-jetty`, `kumuluzee-jax-rs-jersey`, `kumuluzee-cdi-weld`, 
`kumuluzee-jpa-eclipselink` and `org.postgresql` configured `pom.xml`.

Add the `kumuluzee-health` dependency:
```xml
<dependency>
    <groupId>com.kumuluz.ee.health</groupId>
    <artifactId>kumuluzee-health</artifactId>
    <version>${kumuluz-health.version}</version>
</dependency>
```

### Implement Health Check Bean

Implement a class which implements `HealthCheck` and is annotated with `@Health` and `@ApplicationScoped`. The bean 
should contain a method call() which executes health check. The bean will be registered to the HealthRegistry 
automatically and called either by accessing health servlet or by periodic health checks which are logged to the logs.

Sample implementation of such a class (which could also be checked by a built in HttpHealthCheck):

```java
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

@Health
@ApplicationScoped
public class GithubHealthCheckBean implements HealthCheck {

    private static final String url = "https://github.com/kumuluz/kumuluzee";

    private static final Logger LOG = Logger.getLogger(GithubHealthCheckBean.class.getSimpleName());

    @Override
    public HealthCheckResponse call() {
        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");

            if (connection.getResponseCode() == 200) {
                return HealthCheckResponse.named(GithubHealthCheckBean.class.getSimpleName()).up().build();
            }
        } catch (Exception exception) {
            LOG.severe(exception.getMessage());
        }
        return HealthCheckResponse.named(GithubHealthCheckBean.class.getSimpleName()).down().build();
    }
}
```

### Add Health configuration

For registering built in disk space and postgres data source health checks replace config.yaml file content with the 
following 
content:

```yaml
kumuluzee:
  datasources:
    - jndi-name: jdbc/CustomersDS
      connection-url: jdbc:postgresql://localhost:5432/customers
      username: postgres
      password: postgres
      max-pool-size: 20
  health:
    servlet:
      mapping: /health
      enabled: true
    logs:
      enabled: true
      level: INFO
      period-s: 30
    checks:
      data-source-health-check:
        jndi-name: jdbc/CustomersDS
      disk-space-health-check:
        threshold: 100000000
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.

The json output (http://localhost:8080/health) should look similar to the one bellow:
```json
{
  "outcome" : "UP",
  "checks" : [ {
    "name" : "DiskSpaceHealthCheck",
    "state" : "UP"
  }, {
    "name" : "DataSourceHealthCheck",
    "state" : "UP"
  }, {
    "name" : "GithubHealthCheckBean",
    "state" : "UP"
  } ]
}
```