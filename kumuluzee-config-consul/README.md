# KumuluzEE Config sample with Consul 

> Build a REST service which utilizes KumuluzEE Config to access configuration properties stored in Consul and pack it 
as a KumuluzEE microservice

The objective of this sample is to show how to develop a microservice that uses KumuluzEE Config extension to
access configuration properties stored in Consul. In this sample we develop a simple REST service that returns
a list of configuration properties from all available configuration sources and pack it as KumuluzEE microservice. This 
tutorial will guide you through all the necessary steps. You will first add KumuluzEE dependencies into pom.xml. To 
develop the REST service, you will use the standard JAX-RS 2 API. Required knowledge: basic familiarity with JAX-RS 2
and basic concepts of REST, JSON, yaml and Consul.

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

4. Local Consul agent:
    * If you have installed Consul, you can check the version by typing the following in a command line:
    
        ```
        consul version
        ```
    

## Prerequisites

To run this sample you will need a local Consul agent. Note that such setup with Consul running in development mode is
not viable for production environments, but only for developing purposes. Here is an example on how to quickly run a
local Consul agent in development mode:

   ```bash
    $ consul agent -dev -ui
   ```


## Usage

The example uses maven to build and run the microservice.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-config-consul
    $ mvn clean package
    ```

2. Run the sample:
* Uber-jar:

    ```bash
    $ java -jar target/${project.build.finalName}.jar.jar
    ```
    
    in Windows environemnt use the command
    ```batch
    java -jar target/${project.build.finalName}.jar.jar
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
* JAX-RS REST resource - http://localhost:8080/v1/config

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a simple REST service that exposes configuration 
properties retrieved with a built-in configuration framework and KumuluEE config extension. We will develop a 
simple REST service with just one resource:
* GET http://localhost:8080/v1/config - list of all configuration properties from configuration file 

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet, JAX-RS and CDI)
* Add Maven dependency to KumuluzEE Config
* Define our configuration properties in configuration file
* Implement the service using standard JAX-RS 2
* Build the microservice
* Run it
* Dynamically change configuration properties in Consul

### Add Maven dependencies

Add the KumuluzEE BOM module dependency to your `pom.xml` file:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-bom</artifactId>
            <version>${kumuluz.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty`, `kumuluzee-jax-rs-jersey` and `kumuluzee-cdi-weld` dependencies:
```xml
<dependencies>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
         <artifactId>kumuluzee-core</artifactId>
    </dependency>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
         <artifactId>kumuluzee-servlet-jetty</artifactId>
    </dependency>
    <dependency>
         <groupId>com.kumuluz.ee</groupId>
         <artifactId>kumuluzee-jax-rs-jersey</artifactId>
    </dependency>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
        <artifactId>kumuluzee-cdi-weld</artifactId>
    </dependency>
</dependencies>
```

Alternatively, we could add the `kumuluzee-microProfile-1.0`, which adds the MicroProfile 1.0 dependencies (JAX-RS, CDI,
JSON-P, and Servlet).

Add dependency to KumuluzEE Config extension:

```xml
    <dependency>
        <groupId>com.kumuluz.ee.config</groupId>
        <artifactId>kumuluzee-config-consul</artifactId>
        <version>${kumuluzee-config.version}</version>
    </dependency>
```
 
Add the `kumuluzee-maven-plugin` build plugin to package microservice as uber-jar:

```xml
<build>
    <plugins>
        <plugin>
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
        </plugin>
    </plugins>
</build>
```

or exploded:

Add the `kumuluzee-maven-plugin` build plugin to package microservice as exploded:

```xml
<build>
    <plugins>
        <plugin>
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
        </plugin>
    </plugins>
</build>
```

### Implement the service

Define KumuluzEE configuration as well as your custom configuration properties in a `config.yaml` configuration 
file:

```yaml
kumuluzee:
  name: customer-service
  version: 1.0.0
  env:
    name: dev
  config:
    start-retry-delay-ms: 500
    max-retry-delay-ms: 900000

rest-config:
  string-property: Monday
  boolean-property: true
  integer-property: 23
```

Register your module as JAX-RS service and define the application path. You could do that in web.xml or for example 
with the `@ApplicationPath` annotation:

```java
@ApplicationPath("v1")
public class ConfigApplication extends Application {
}
```

Implement an application scoped CDI bean that will automatically load and hold our configuration properties. It shall
be annotated with `@ConfigBundle` annotation whose value represents the prefix for the configuration properties keys.
Add a `@ConfigValue(watch = true)` to enable watch on the key. This will monitor the changes of this key in Consul and 
automatically update the value in the configuration bean. 
 
```java
@ApplicationScoped
@ConfigBundle("rest-config")
public class ConfigProperties {

    @ConfigValue(watch = true)
    private String stringProperty;
    private Boolean booleanProperty;
    private Integer integerProperty;
    
    // get and set methods
    
}
```

Implement a JAX-RS resource that will expose retrieved configuration properties on GET request:

```java
@RequestScoped
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConfigResource {

    @Inject
    private ConfigProperties properties;

    @GET
    @Path("/config")
    public Response test() {
        String response =
                "{" +
                        "\"stringProperty\": \"%s\"," +
                        "\"booleanProperty\": %b," +
                        "\"integerProperty\": %d" +
                        "}";

        response = String.format(
                response,
                properties.getStringProperty(),
                properties.getBooleanProperty(),
                properties.getIntegerProperty());

        return Response.ok(response).build();
    }
}
```

To build the microservice and run the example, use the commands as described in previous sections.

Since we have not defined any configuration properties in Consul, GET http://localhost:8080/v1/config will return 
configuration properties from configuraiton file. We can now try and some values in Consul. Since we enabled watch on 
the field `stringProperty`, it will be dynamically updated on any change in Consul. We can add a value to Consul from
the user interface, which can be accessed at `http://localhost:8500`.

To set a value, navigate to `KEY/VALUE` tab and create key 
`environments/dev/services/customer-service/1.0.0/config/rest-config/string-property` 
with a value of your own choosing.

Access the config endpoint again and you will get an updated value from Consul.
