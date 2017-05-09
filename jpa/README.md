# KumuluzEE servlet sample

> JPA and CDI usage sample with KumuluzEE

This sample demonstrates how to configure and use Java EE JPA and CDI technologies using KumuluzEE.

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
    $ cd jpa
    $ mvn clean package
    ```

2. Run the sample:

    ```bash
    $ java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
The application/service can be accessed on the following URL:
* JAX-RS REST resource page - http://localhost:8080/v1/customers

To shut down the example simply stop the processes in the foreground.

## Tutorial
This tutorial will guide you through the steps required to create a simple REST microservice which uses JPA 2.1 and pack it as a KumuluzEE microservice. We will extend the [existing KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs), with the access to database using JPA 2.1. Therefore, first complete the existing JAX-RS sample tutorial, or clone the JAX-RS sample code. We will use PostgreSQL in this tutorial.

We will follow these steps:
* Complete the [tutorial for KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) or clone the existing sample
* Ensure access to PostgreSQL database.
* Add Maven dependencies
* Implement the persistence using standard JPA
* Build the microservice
* Run it

### Add Maven dependencies

Since your existing starting point is the existing KumuluzEE JAX-RS REST sample, you should already have the dependencies for `kumuluzee-bom`, `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-jax-rs-jersey` configured in `pom.xml`.

Add the `kumuluzee-cdi-weld`, `kumuluzee-jpa-eclipselink` and `postgresql` dependencies:
```xml
   
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-cdi-weld</artifactId>
</dependency>
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-jpa-eclipselink</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.0.0</version>
</dependency>
    
```

### Implement database access layer

Enhance existing `Customer` class with JPA annotations:
* Add `@Entity` and `@Table` to make it persistable. 
* Add `Customer.findCustomers` named query for retrieving all customers from database
* Mark attribute `id` as primary key.
* Change the default database column mapping

```java
@Entity
@Table(name = "customer")
@NamedQueries({
        @NamedQuery(
                name = "Customer.findCustomers",
                query = "SELECT c " +
                        "FROM Customer c"
        )
})
public class Customer implements Serializable {

    @Id
    private String id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;

    //TODO: get and set methods
}
```

Implement `CustomerService` class. Implement it as a `@RequestScoped` CDI bean, and inject `EntityManager`. Use `EntityManager` to implement the following methods:

```java
public Customer getCustomer(String customerId) 
public List<Customer> getCustomers() 
public void saveCustomer(Customer customer)
public void deleteCustomer(String customerId) 
```
Sample implementation of `CustomerService` class:

```java
@RequestScoped
public class CustomerService {

    @PersistenceContext
    private EntityManager em;

    public Customer getCustomer(String customerId) {
        return em.find(Customer.class, customerId);
    }

    public List<Customer> getCustomers() {
        List<Customer> customers = em
                .createNamedQuery("Customer.findCustomers", Customer.class)
                .getResultList();

        return customers;
    }

    public void saveCustomer(Customer customer) {
        try {
            beginTx();
            em.persist(customer);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }
    }

    public void deleteCustomer(String customerId) {
        Customer customer = em.find(Customer.class, customerId);
        if (customer != null) {
            try {
                beginTx();
                em.remove(customer);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        }
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }
}
```

### Implement REST Service

Make `CustomerResource` class a CDI bean by adding `@RequestScoped` annotation. Inject newly, created `CustomerService` to `CustomerResource` using `@Inject` annotation. In every resource method implementation, replace the invocation of static `Database` class with invocation of injected `CustomerService` implementation. Sample implementation:

```java
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
public class CustomerResource {

    @Inject
    private CustomerService customerBean;

    @GET
    public Response getAllCustomers() {
        List<Customer> customers = customerBean.getCustomers();
        return Response.ok(customers).build();
    }

    @GET
    @Path("{customerId}")
    public Response getCustomer(@PathParam("customerId") String customerId) {
        Customer customer = customerBean.getCustomer(customerId);
        return customer != null
                ? Response.ok(customer).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response addNewCustomer(Customer customer) {
        customerBean.saveCustomer(customer);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{customerId}")
    public Response deleteCustomer(@PathParam("customerId") String customerId) {
        customerBean.deleteCustomer(customerId);
        return Response.noContent().build();
    }
}
```


### Configure CDI

Create directory `resources/META-INF`. In this directory create file `beans.xml`, with the following content to enable CDI:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/beans_1_2.xsd"
       bean-discovery-mode="annotated">
</beans>
```

### Configure database and persistence

Make sure, that you have database server prepared, as described in Prerequisites section.

In directory `resources/META-INF` create file `persistence.xml`: 

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
             http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd"
             version="2.1">
    <persistence-unit name="kumuluzee-samples-jpa" transaction-type="RESOURCE_LOCAL">

        <non-jta-data-source>jdbc/CustomersDS</non-jta-data-source>

        <class>com.kumuluz.ee.samples.jpa.Customer</class>

        <properties>
            <property name="javax.persistence.schema-generation.database.action" value="create"/>
            <property name="javax.persistence.schema-generation.create-source" value="metadata"/>
            <property name="javax.persistence.schema-generation.drop-source" value="metadata"/>
        </properties>

    </persistence-unit>
</persistence>
```

Modify the element `class` in the above example, to reflect the package and class name of entity `Customer` in your source code structure.

In directory `resources/META-INF` add file `config.xml` with database connectivity properties:

```xml
kumuluzee:
  datasources:
    - jndi-name: jdbc/CustomersDS
      connection-url: jdbc:postgresql://localhost:5432/customers
      username: postgres
      password: postgres
      max-pool-size: 20
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.