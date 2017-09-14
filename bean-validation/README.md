# KumuluzEE bean validation sample

> Use bean validation within a REST service and pack it as a KumuluzEE microservice.

The objective of this sample is to demonstrate how to use bean validation. The tutorial shows you how to add bean validation annotations to existing classes. You will add KumuluzEE dependencies into pom.xml. Required knowledge: basic familiarity with bean validation.

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
    $ cd bean-validation
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
* JAX-RS REST resource page - http://localhost:8080/v1/customers

To shut down the example simply stop the processes in the foreground.

## Tutorial
This tutorial will guide you through the steps required to create a simple REST microservice which uses bean validation and pack it as a KumuluzEE microservice. We will extend the existing [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) add bean validation annotations to existing classes. 
Therefore, first complete the existing JAX-RS sample tutorial, or clone the JAX-RS sample code.

We will follow these steps:
* Complete the tutorial for [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) or clone the existing sample
* Add Maven dependencies
* Add bean validation annotations
* Build the microservice
* Run it

### Add Maven dependencies

Since your existing starting point is the existing KumuluzEE JAX-RS REST sample, you should already have the dependencies for `kumuluzee-bom`, `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-jax-rs-jersey` configured in `pom.xml`.

Add the `kumuluz-el-uel` and `kumuluzee-bean-validation-hibernate-validator` dependencies:
```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-el-uel</artifactId>
</dependency>
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-bean-validation-hibernate-validator</artifactId>
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

Add the `kumuluzee-maven-plugin` build plugin to package microservice as exploded:

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

### Add bean validation annotations

Add bean validation annotations, such as `@NotNull`, `@Size`, `@Min` ... to the `Customer` class.
 Also add additional fields with different data types, and try some other annotations as well.
Sample implementation:

```java
public class Customer {

    @NotNull
    private String id;
    @NotNull
    @Size(min = 1, max = 25)
    private String firstName;
    @NotNull
    @Size(min = 1, max = 50)
    private String lastName;
    @NotNull
    @Min(18)
    @Max(99)
    private int age;
    @NotNull
    @Past
    private Date birthday;
    @NotNull
    @AssertTrue
    private boolean active;
    
    ...
    
    // get and set methods
}
```

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
public class CustomerResource {
  
    @POST
    public Response addNewCustomer(@Valid Customer customer) {
        Database.addCustomer(customer);
        return Response.noContent().build();
    }
    
    ...
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
