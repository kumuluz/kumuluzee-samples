# KumuluzEE Logs sample with JUL implementation

> Build a REST service which utilizes a built-in logging framework with JUL implementation to log basic metrics and pack it as a KumuluzEE microservice

The objective of this sample is to demonstrate how to use the built-in logging framework with JUL implementation to log basic metrics.

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
    $ cd kumuluzee-logs-jul
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

To shut down the example simply stop the processes in the foreground.

## Tutorial
This tutorial will guide you through the steps required to use KumuluzEE Logs and pack the application as a KumuluzEE microservice. We will extend the existing [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs).
Therefore, first complete the existing JAX-RS sample tutorial, or clone the JAX-RS sample code.

We will follow these steps:
* Complete the tutorial for [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) or clone the existing sample
* Add Maven dependencies
* Add KumuluzEE Logs support
* Add JUL logging configuration
* Build the microservice
* Run it

### Add Maven dependencies

Since your existing starting point is the existing KumuluzEE JAX-RS REST sample, you should already have the dependencies for `kumuluzee-bom`, `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-jax-rs-jersey` configured in `pom.xml`.

Add the `kumuluzee-cdi-weld` and `kumuluzee-logs-jul` dependencies:
```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-cdi-weld</artifactId>
</dependency>
<dependency>
    <groupId>com.kumuluz.ee.logs</groupId>
    <artifactId>kumuluzee-logs-jul</artifactId>
    <version>${kumuluz-logs.version}</version>
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

### Add JUL logging configuration

The default java.util.logging configuration can be overridden by providing the location of the custom configuration 
file with system property `-Djava.util.logging.config.file`.

In this sample in directory `resources` create file `logging.properties`: 

```
# Default global logging level
.level=FINER

# ConsoleHandler definition
handlers=java.util.logging.ConsoleHandler

# ConsoleHandler configuration settings
java.util.logging.ConsoleHandler.level=FINER
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter
```

To use this configuration instead of the default one provide system property `-Djava.util.logging.config.file` when 
running the application:

```bash
$ java -Djava.util.logging.config.file=<path>/kumuluzee-samples/kumuluzee-logs-jul/src/main/resources/logging.properties -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
```

in Windows environment use the command
```batch
java -Djava.util.logging.config.file=<path>/kumuluzee-samples/kumuluzee-logs-jul/src/main/resources/logging.properties -cp target/classes;target/dependency/* com.kumuluz.ee.EeApplication
```

and replace the `<path>` with appropriate directory path.

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
