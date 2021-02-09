# KumuluzEE GraphQL sample with JPA, CDI and Security

> Enable security on a GraphQL microservice.

The objective of this sample is to demonstrate how to add security to your existing GraphQL endpoint. This sample 
builds on previous sample: [KumuluzEE JPA and CDI with GraphQL](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-graphql-jpa-simple).

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
You can run databases with docker:
```bash
docker run -d --name pg-graphql -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=customers -p 5432:5432 postgres:latest
```

Due to added security, running Keycloak is also required. You can run it with docker:

```bash
$ docker run -p 8082:8080 \
        -e KEYCLOAK_USER=admin \
        -e KEYCLOAK_PASSWORD=admin \
        -e DB_VENDOR=h2 \
        quay.io/keycloak/keycloak:11.0.2
```

## Usage

The example uses maven to build and run the microservices.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-graphql-jpa-security
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

> Postman is recommended for making requests due to GraphiQL not supporting sending Bearer tokens. GraphiQL can still 
> be used for testing query syntax and for checking the structure of requests.

## Tutorial

This tutorial will guide you through the steps required to create a simple GraphQL microservice with security and 
pack it as a KumuluzEE microservice. We will extend the existing 
[KumuluzEE JPA and CDI sample with GraphQL](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-graphql-jpa-simple).
Therefore, first complete the existing sample tutorial, or clone the sample code. We will use PostgreSQL in this
tutorial.

We will follow these steps:
* Complete the tutorial for [KumuluzEE JPA and CDI with GraphQL](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-graphql-jpa-simple) or clone the existing sample
* Add Maven dependencies
* Configure Keycloak
* Add GraphQLApplication class
* Implement security
* Build the microservice
* Run it

### Add Maven dependencies

Since your starting point is the existing `KumuluzEE JPA and CDI sample with GraphQL`, you should already have the
dependencies for `kumuluzee-bom`, `kumuluzee-core`, `kumuluzee-servlet-jetty`, `kumuluzee-jax-rs-jersey`,
`kumuluzee-cdi-weld`, `kumuluzee-jpa-eclipselink`, `kumuluzee-graphql-mp`, `kumuluzee-json-p-jsonp`,
`kumuluzee-json-b-yasson`, `kumuluzz-graphql-ui` and `postgresql` configured in `pom.xml`.


Add `kumuluzee-security` and `Keycloak Jetty adapter`:

```xml
<dependency>
    <groupId>com.kumuluz.ee.security</groupId>
    <artifactId>kumuluzee-security-keycloak</artifactId>
    <version>${kumuluzee-security.version}</version>
</dependency>

<dependency>
     <groupId>org.keycloak</groupId>
     <artifactId>keycloak-jetty94-adapter</artifactId>
     <version>${keycloak.version}</version>
 </dependency>
```

`kumuluzee-maven-plugin` should already be added to your project from JPA and CDI sample.

### Configure Keycloak

Follow the [Configure Keycloak](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-security-keycloak#configure-keycloak)
steps from `kumuluzee-security-keycloak` sample.

### Add GraphQLApplication class

Before we can enable security on GraphQL endpoint, we need to add a custom class, which extends GraphQLApplication class.
Annotation `GraphQLApplicationClass` is also required.

```java
@GraphQLApplicationClass
public class CustomerApp extends GraphQLApplication {}
```

### Implement security

In order to enable security on our GraphQL endpoint, we need to do the following:

- annotate GraphQLApplication class with `@DeclareRoles` annotation
```java
@GraphQLApplicationClass
@DeclareRoles({"user", "admin"})
public class CustomerApp extends GraphQLApplication {}
```

- annotate desired GraphQL classes with `@Secure` annotation (the classes we want protected)
```java
@RequestScoped
@GraphQLApi
@Secure
public class CustomerResource { ... }
```

- annotate queries and mutations with security annotations (`PermitAll`, `RolesAllowed`, `DenyAll`...)
```java
@RequestScoped
@GraphQLApi
@Secure
public class CustomerResource {

    @Inject
    private CustomerService customerBean;

    @Query
    @PermitAll
    public List<Customer> getAllCustomers() {
       return customerBean.getCustomers();
    }

    @Query
    @RolesAllowed({"user", "admin"})
    public Customer getCustomer(@Name("customerId") String customerId) {
        return customerBean.getCustomer(customerId);
    }

    @Mutation
    @RolesAllowed("admin")
    public Customer addNewCustomer(@Name("customer") Customer customer) {
        customerBean.saveCustomer(customer);
        return customer;
    }

    @Mutation
    @DenyAll
    public void deleteCustomer(@Name("customerId") String customerId) {
        customerBean.deleteCustomer(customerId);
    }
}
```
With these annotations, we have now protected GraphQL endpoint. With `PermitAll`, all authorized requests are permitted.
`RolesAllowed` allows us to specify which user roles can access specific operations. If we want to forbid using
specific operations, we can use `DenyAll`.

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.

### Querying endpoint

After following these steps, the GraphQL endpoint can now be queried with Bearer token. To get the token, send a
request to Keycloak auth endpoint: `http://{keycloakhost:port}/auth/realms/{realmname}/protocol/openid-connect/token`
(`http://localhost:8082/auth/realms/customers-realm/protocol/openid-connect/token`).

All secured requests should now include `Authorization` header with content: `Bearer {token}`.
