
# KumuluzEE Feature Flags sample with Unleash

The objective of this sample is to show how to use feature flags in KumuluzEE. You will add KumuluzEE dependencies into pom.xml. You will create configuration and check if the flags are enabled using Unleash client.

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
3. Unleash:
    * run Unleash with docker, you can find a guide [here](https://github.com/Unleash/unleash-docker)

## Usage

The example uses maven to build and run the microservices.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-feature-flags-unleash
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
* JAX-RS REST resource page - http://localhost:8080/v1/features

To shut down the example simply stop the processes in the foreground.

## Tutorial
This tutorial will guide you through the steps required to connect to a feature flag server and use it's flags. We will extend the existing [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) to test if the flags are enabled or disabled. Therefore, first complete the existing JAX-RS sample tutorial, or clone the JAX-RS sample code.

We will follow these steps:
* Complete the tutorial for [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) or clone the existing sample
* Ensure access to Unleash server
* Add Maven dependencies
* Build the microservice
* Run it

### Add Maven dependencies

Since your existing starting point is the existing KumuluzEE JAX-RS REST sample, you should already have the dependencies for `kumuluzee-bom`, `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-jax-rs-jersey` configured in the `pom.xml`.

Add the `feature-flags-unleash` dependency:
```xml
<dependency>  
 <groupId>com.kumuluz.ee</groupId>  
 <artifactId>feature-flags-unleash</artifactId>  
 <version>${feature-flags-unleash.version}</version>  
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

### Configure Unleash connection

In the directory `resources` edit the file `config.yaml` by adding the following RabbitMQ properties:

```yaml
kumuluzee:
  name: feature-flags-sample-service
  version: 1.0.0
  env:
    name: dev
  feature-flags:
    unleash:
      unleash-api: "http://localhost:4242/api"
```

Optionally, you can create UnleashConfig object in a class annotated with `@FFConnection`

```java
@FFConnection  
@ApplicationScoped  
public class UnleashConnection {  
    UnleashConfig config = UnleashConfig  
            .builder()  
            .unleashAPI("http://localhost:4242/api")  
            .appName("feature-flags-sample-service")  
            .build();  
}
```

### Creating a flag

Access the admin UI on http://localhost:4242 and create a new flag. Name it "test-feature" (or whatever you want, but don't forget to correct the flag name in the code after).

### Using feature flags

You can check if feature flags are enabled by using FeatureFlags object in the same way as you would use Unleash client object.

```java
@Inject
private FeatureFlags featureFlags;

if(featureFlags.isEnabled("test-feature"){
    //do something
} else {
    //do something else
}
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
