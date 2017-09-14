# KumuluzEE REST sample

> Expand the KumuluzEE JPA and CDI sample with REST query parameters for automatic pagination, sorting and filtering.

The objective of this sample is to demonstrate how to expand your REST service to incorporate query parameters for automatic pagination, sorting and filtering of JPA entities. The tutorial expands the development of JPA sample. You will add KumuluzEE dependency into pom.xml. You will add query parameters parsing from the URI in the CustomerResource class and entity querying based on the parameters in the CustomerService CDI class. Required knowledge: basic familiarity with JPA, CDI and basic concepts of REST and JSON.

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
    $ cd kumuluz-rest
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
This tutorial will guide you through the steps required to extend a simple REST microservice with REST query 
parameters and pack it as a KumuluzEE microservice. We will extend the existing [KumuluzEE JPA sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jpa), with REST query parameters. Therefore, first complete the existing 
JPA sample tutorial, or clone the JPA sample code.

We will follow these steps:
* Complete the tutorial for [KumuluzEE JPA sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jpa) or clone the existing sample
* Ensure access to PostgreSQL database.
* Add Maven dependencies
* Parse query parameters from the URI in Rest resource
* Query entities based on the extracted query parameters in the CDI bean
* Build the microservice
* Run it

### Add Maven dependencies

Since your existing starting point is the existing KumuluzEE JPA sample, you should already have the dependencies for `kumuluzee-bom`, `kumuluzee-core`, `kumuluzee-servlet-jetty`, `kumuluzee-jax-rs-jersey`, `kumuluzee-cdi-weld`, `kumuluzee-jpa-eclipselink` and `postgresql` configured in `pom.xml`.

Add the `kumuluzee-rest-core` dependency:
```xml
<dependency>
    <groupId>com.kumuluz.ee.rest</groupId>
    <artifactId>kumuluzee-rest-core</artifactId>
    <version>${kumuluzee-rest.version}</version>
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

### Extend REST Service with parsing the query parameters from the URI

First you will need to extend the existing CustomerResource with URI context information. The URI can be obtained by adding UriInfo context to selected Resource:

```java
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
public class CustomerResource {

    @Context
    protected UriInfo uriInfo;
    
    ...
    
}
```

Using the URI context information the query parameters can be constructed by using the QueryParameters class:

```java
    @GET
    public Response getAllCustomers() {
        List<Customer> customers = customerBean.getCustomers(createQuery());
        return Response.ok(customers).build();
    }
    
    @GET
    @Path("count")
    public Response getCount() {
        Long count = customerBean.getCustomerCount(createQuery());
        return Response.ok(count).build();
    }
    
    private QueryParameters createQuery() {
        return QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0).defaultLimit(10).build();
    }
```

### Extend CDI bean by using the query parameters for querying the entities

After parsing the query parameters they can be used to query or count entities using the `JPAUtils` class:
```java
@RequestScoped
public class CustomerService {

    @PersistenceContext
    private EntityManager em;

    public List<Customer> getCustomers(QueryParameters query) {
        List<Customer> customers = JPAUtils.queryEntities(em, Customer.class, query);
        return customers;
    }


    public Long getCustomerCount(QueryParameters query) {
        Long count = JPAUtils.queryEntitiesCount(em, Customer.class, query);
        return count;
    }
    
    ...
}
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
