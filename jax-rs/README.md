# KumuluzEE JAX-RS REST service sample

> Develop a REST service using standard JAX-RS 2 API and pack it as a KumuluzEE microservice.

The objective of this sample is to show how to develop a REST service using standard JAX-RS 2 API and pack it as a KumuluzEE microservice. The tutorial will guide you through the necessary steps. You will add KumuluzEE dependencies into pom.xml. To develop the REST service, you will use the standard JAX-RS 2 API. 
Required knowledge: basic familiarity with JAX-RS 2 and basic concepts of REST and JSON.

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
    $ cd jax-rs
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
* JAX-RS REST resource - http://localhost:8080/v1/customers

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a simple REST service using standard JAX-RS 2 API and pack it as a KumuluzEE microservice. 
We will develop a simple Customer REST service with the following resources:
* GET http://localhost:8080/v1/customers - list of all customers 
* GET http://localhost:8080/v1/customers/{customerId} – details of customer with ID {customerId}
* POST http://localhost:8080/v1/customers – add a customer
* DELETE http://localhost:8080/v1/customers/{customerId} – delete customer with ID {customerId}

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet and JAX-RS)
* Implement the service using standard JAX-RS 2 API
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

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-jax-rs-jersey` dependencies:
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
</dependencies>
```

Alternatively, we could add the `kumuluzee-microProfile-1.0`, which adds the MicroProfile 1.0 dependencies (JAX-RS, CDI, JSON-P, and Servlet).

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

Register your module as JAX-RS service and define the application path. You could do that in web.xml or for example with `@ApplicationPath` annotation:

```java
@ApplicationPath("v1")
public class CustomerApplication extends Application {
}
```

Implement JAX-RS resource, for example, to implement resource `customers` which will return all customers by default on GET request:

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
public class CustomerResource {

    @GET
    public Response getAllCustomers() {
        List<Customer> customers = Database.getCustomers();
        return Response.ok(customers).build();
    }

    @GET
    @Path("{customerId}")
    public Response getCustomer(@PathParam("customerId") String customerId) {
        Customer customer = Database.getCustomer(customerId);
        return customer != null
                ? Response.ok(customer).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response addNewCustomer(Customer customer) {
        Database.addCustomer(customer);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{customerId}")
    public Response deleteCustomer(@PathParam("customerId") String customerId) {
        Database.deleteCustomer(customerId);
        return Response.noContent().build();
    }
}
```

Implement the `Customer` Java class, which is a POJO:
```java
public class Customer {

    private String id;

    private String firstName;

    private String lastName;

    // TODO: implement get and set methods
}
```

In the example above, we use `Database` class to access data. A sample implementation which simulates persistance layer, can be implemented as follows:

```java
public class Database {
    private static List<Customer> customers = new ArrayList<>();

    public static List<Customer> getCustomers() {
        return customers;
    }

    public static Customer getCustomer(String customerId) {
        for (Customer customer : customers) {
            if (customer.getId().equals(customerId))
                return customer;
        }

        return null;
    }

    public static void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public static void deleteCustomer(String customerId) {
        for (Customer customer : customers) {
            if (customer.getId().equals(customerId)) {
                customers.remove(customer);
                break;
            }
        }
    }
}
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
