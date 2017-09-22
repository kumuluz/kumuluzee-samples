# KumuluzEE MicroProfile Config sample

> Build a JAX-RS service which utilizes the KumuluzEE MicroProfile configuration to access configuration values through 
MicroProfile Config API and pack it as a KumuluzEE microservice

The objective of this sample is to show how to develop a microservice that uses the MicroProfile Config API to
access configuration values. In this sample we develop a simple JAX-RS service that returns
a list of configuration properties from configuration file and pack it as KumuluzEE microservice. This tutorial will 
guide you through all the necessary steps. You will first add KumuluzEE dependencies into pom.xml. You will then
implement a JAX-RS Resource, which will expose some configuration values. Required knowledge: basic familiarity with
JAX-RS 2.

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

The example uses maven to build and run the microservice.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-config-mp
    $ mvn clean package
    ```

2. Run the sample:
* Uber-jar:

    ```bash
    $ java -Dcom.kumuluz.ee.configuration.file="META-INF/microprofile-config.properties" -jar target/${project.build.finalName}.jar
    ```
    
    in Windows environment use the command
    ```batch
    java -Dcom.kumuluz.ee.configuration.file="META-INF/microprofile-config.properties" -jar target/${project.build.finalName}.jar
    ```

* Exploded:

    ```bash
    $ java -Dcom.kumuluz.ee.configuration.file="META-INF/microprofile-config.properties" -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
    in Windows environment use the command
    ```batch
    java -Dcom.kumuluz.ee.configuration.file="META-INF/microprofile-config.properties" -cp target/classes;target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
    
The application/service can be accessed on the following URL:
* JAX-RS Resource - http://localhost:8080/v1/config

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a simple JAX-RS service that exposes configuration 
properties retrieved with the MicroProfile Config API and pack it as a KumuluzEE microservice. We will develop a 
simple JAX-RS Resource:
* GET http://localhost:8080/v1/config - list of configuration properties from configuration file 

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet, CDI, JAX-RS and MicroProfile Config)
* Define our configuration properties in configuration file
* Implement the JAX-RS service
* Add a custom Converter
* Build the microservice
* Run it

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

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty`, `kumuluzee-cdi-weld`, `kumuluzee-jax-rs-jersey` and
`kumuluzee-config-mp` dependencies:
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
        <artifactId>kumuluzee-cdi-weld</artifactId>
    </dependency>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
        <artifactId>kumuluzee-jax-rs-jersey</artifactId>
    </dependency>
    <dependency>
        <groupId>com.kumuluz.ee.config</groupId>
        <artifactId>kumuluzee-config-mp</artifactId>
        <version>1.1.0</version>
    </dependency>
</dependencies>
```

Alternatively, we could add the `kumuluzee-microProfile-1.0`, which adds the MicroProfile 1.0 dependencies (JAX-RS, CDI,
JSON-P, and Servlet).

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

### Define configuration values

Define your configuration properties in a `META-INF/microprofile-config.properties` configuration file:

```properties
mp.exampleString = Hello MicroProfile Config!
mp.exampleBoolean = true
mp.exampleCustomer = John:Doe
```

### Implement the JAX-RS service

Register your module as JAX-RS service and define the application path. You could do that in web.xml or for example 
with the `@ApplicationPath` annotation:

```java
@ApplicationPath("/v1")
public class ConfigApplication extends Application {
}
```

Implement the JAX-RS Resource, which will read the configuration values through various mechanisms, offered by the
MicroProfile Config API.

```java
@RequestScoped
@Produces(MediaType.TEXT_PLAIN)
@Path("config")
public class ConfigResource {

    @GET
    public Response testConfig() {
        StringBuilder response = new StringBuilder();



        return Response.ok(response.toString()).build();
    }
}
```

Configuration values can be accessed through the `Config` object. You can get the `Config` object programmatically.
To do so, add the following lines to the `testConfig` method:

```java
Config config = ConfigProvider.getConfig();
response.append(config.getValue("mp.exampleString", String.class)).append('\n');
```

The `Config` object can also be acquired with CDI injection. Add the following lines to your Resource implementation:

```java
@Inject
private Config injectedConfig;
```

To use the injected `Config` object, add the following line to the `testConfig` method:

```java
response.append(injectedConfig.getValue("mp.exampleBoolean", boolean.class)).append('\n');
```

You can also use the `@ConfigProperty` annotation to inject configuration values directly.
Injection is supported for all types, listed in MicroProfile Config specification.
Annotation also supports the `defaultValue` parameter, which is used, when configuration property in not present.
If `defaultValue` is not specified and configuration property is not present, injection will throw
`DeploymentException`. Add the following lines to your Resource implementation:

```java
@Inject
@ConfigProperty(name = "mp.exampleString")
private String injectedString;

@Inject
@ConfigProperty(name = "mp.non-existent-string", defaultValue = "Property does not exist!")
private String nonExistentString;
```

To output the injected values, add the following lines to the `testConfig` method:

```java
response.append(injectedString).append('\n');
response.append(nonExistentString).append('\n');
```

Alternatively to `defaultValue` parameter, you can inject configuration values as an `Optional` object. Add the
following lines to your Resource implementation:

```java
@Inject
@ConfigProperty(name = "mp.non-existent-string")
private Optional<String> nonExistentStringOpt;
```

To output the injected value, add the following line to the `testConfig` method:

```java
response.append(nonExistentStringOpt.orElse("Empty Optional")).append('\n');
```

### Add custom Converter

Creating custom Converters enables you to inject configuration values in the field types of your choice.

First create a `Customer` class, which is a POJO:

```java
public class Customer {

    private String name;
    private String surname;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public static Customer parse(String s) {
        String[] spl = s.split(":");

        if(spl.length == 2) {
            Customer c = new Customer();
            c.setName(spl[0]);
            c.setSurname(spl[1]);

            return c;
        }

        return null;
    }

    @Override
    public String toString() {
        return "Customer with name " + name + " and surname " + surname;
    }
}
```

Create a custom Converter for your `Customer` class:

```java
@Priority(500)
public class CustomerConverter implements Converter<Customer> {

    @Override
    public Customer convert(String s) {
        return Customer.parse(s);
    }
}
```

You can create multiple Converters for the same type. Converter with the highest priority will be used. Priority is
read from the `@Priority` annotation.

In order for Converter to be discovered, you need to register it in the
`/META-INF/services/org.eclipse.microprofile.config.spi.Converter` file with the fully qualified class name:

```text
com.kumuluz.ee.samples.converters.CustomerConverter
```

Inject a `Customer` instance in your Resource implementation:
```java
@Inject
@ConfigProperty(name = "mp.exampleCustomer")
private Customer customer;
```

To output the injected `Customer`, add the following line to the `testConfig` method:

```java
response.append(customer).append('\n');
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
