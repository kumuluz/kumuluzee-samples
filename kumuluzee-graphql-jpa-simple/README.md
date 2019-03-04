# KumuluzEE JPA and CDI with GraphQL

> Develop JPA entities and use CDI within a GraphQL service and pack it as a KumuluzEE microservice.

The object of of this sample is to demonstrate, how to convert your existing JPA and CDI application to expose GraphQL API instead of REST. Before starting this tutorial, please make sure, that you have finished the tutorial this one is based on: [KumuluzEE JPA and CDI with REST](https://github.com/kumuluz/kumuluzee-samples/tree/master/jpa).

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
You can run databases inside docker:
```
docker run -d --name pg-graphql -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=customers -p 5432:5432 postgres:latest
```

## Usage

The example uses maven to build and run the microservices.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-graphql-jpa-simple
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
* GraphQL endpoint - http://localhost:8080/graphql
* GraphiQL endpoint - http://localhost:8080/graphiql (playground for executing queries)

To shut down the example simply stop the processes in the foreground.

## Tutorial
This tutorial will guide you through the steps required to create a simple GraphQL microservice which uses JPA 2.1 and pack it as a KumuluzEE microservice. We will extend the existing [KumuluzEE JPA and CDI sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jpa). Therefore, first complete the existing sample tutorial, or clone the JPA and CDI sample code. We will use PostgreSQL in this tutorial.

We will follow these steps:
* Complete the tutorial for [KumuluzEE JPA and CDI sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jpa) or clone the existing sample
* Add Maven dependencies
* Remove JAX-RS annotations and replace them with GraphQL annotations
* Build the microservice
* Run it

### Add Maven dependencies

Since your existing starting point is the existing KumuluzEE JPA and CDI sample, you should already have the dependencies for `kumuluzee-bom`, `kumuluzee-core`, `kumuluzee-servlet-jetty`, `kumuluzee-jax-rs-jersey`, `kumuluzee-cdi-weld`, `kumuluzee-jpa-eclipselink` and `postgresql` configured in `pom.xml`.


Add the `kumuluzee-graphql` and `kumuluzz-graphql-ui` dependencies:
```xml
<dependency>
    <groupId>com.kumuluz.ee.graphql</groupId>
    <artifactId>kumuluzee-graphql</artifactId>
    <version>${kumuluzee-graphql.version}</version>
</dependency>
<dependency>
    <groupId>com.kumuluz.ee.graphql</groupId>
    <artifactId>kumuluzee-graphql-ui</artifactId>
    <version>${kumuluzee-graphql.version}</version>
</dependency>

```

`kumuluzee-maven-plugin` should already be added to your project from JPA and CDI sample.


### Converting REST service to GraphQL endpoint
Here are the required steps:
- Delete CustomerApplication.java file, because we will no longer be using the REST endpoint
- Replace REST annotations (`@Consumes @Produces and @Path`) with `@GraphQLClass` annotation
- Register API endpoints to become GraphQL queries and mutations: replace `@GET` annotations with `@GraphQLQuery` and `@POST`, `@DELETE` annotations with `@GraphQLMutation`
- Remove all `@Path` annotations
- Replace parameters annotations (`@PathParam("name")` with `@GraphQLArgument(name="name")`)
- Replace output types (we are not returning `Response` anymore but actual types; e.g. getAllCustomers should return `List<Customer>` and not `Response`)


The final code should look something like this:
```java
@RequestScoped
@GraphQLClass
public class CustomerResource {
  
    @Inject
    private CustomerService customerBean;
  
    @GraphQLQuery
    public List<Customer> getAllCustomers() {
       return customerBean.getCustomers();
    }
  
    @GraphQLQuery
    public Customer getCustomer(@GraphQLArgument(name="customerId") String customerId) {
        return customerBean.getCustomer(customerId);
    }
  
    @GraphQLMutation
    public Customer addNewCustomer(@GraphQLArgument(name="customer") Customer customer) {
        customerBean.saveCustomer(customer);
        return customer;
    }
  
    @GraphQLMutation
    public void deleteCustomer(@GraphQLArgument(name="customerId") String customerId) {
        customerBean.deleteCustomer(customerId);
    }
}

```

### Executing queries
You can now execute selected queries:
```
query getAllCustomers {
  allCustomers {
    id
    firstName
    lastName
  }
}
  
query getCustomerById {
  customer(customerId: "1") {
    id
    firstName
    lastName
  }
}
  
mutation addCustomer {
  addNewCustomer(customer: {id: "1", firstName: "Gary", lastName: "Bartlett"}) {
    id
    firstName
    lastName
  }
}
  
mutation deleteCustomer {
  deleteCustomer(customerId: "2")
}
  
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
