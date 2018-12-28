# KumuluzEE OpenTracing Sample

> Simple example on how to use KumuluzEE OpenTracing extension.

Example demonstrates how to take advantage of distributed tracing 
in KumuluzEE projects.

## Requirements

In order to run this example you will need the following:

1. Java 8 (or newer), you can use any implementation:
    * If you have installed Java, you can check the version by typing the following in a command line:
        
        ```bash
        $ java -version
        ```

2. Maven 3.2.1 (or newer):
    * If you have installed Maven, you can check the version by typing the following in a command line:
        
        ```bash
        $ mvn -version
        ```
        
3. Docker 1.13.0 (or newer):
    * If you have installed Docker, you can check the version by typing the following in a command line:
    
        ```bash
        $ docker --version
        ```

## Jaeger Tracing

### Prerequisites

Make sure you have Jaeger tracing instance running.

```bash
$ docker run -d -p 5775:5775/udp -p 16686:16686 jaegertracing/all-in-one:latest
```


## Zipkin tracing

### Prerequisites

Make sure you have Zipkin tracing instance running.

```bash
$ docker run -d -p 9411:9411 openzipkin/zipkin
```

By default Jaeger tracing is enabled in both sample microservices. 
To enable Zipkin tracing all you need to do is change dependency in
customers and orders module:

```xml
<!--Replace kumuluzee-opentracing-jaeger with this dependency.-->
<dependency>
    <groupId>com.kumuluz.ee.opentracing</groupId>
    <artifactId>kumuluzee-opentracing-zipkin</artifactId> 
    <version>${kumuluzee-opentracing.version}</version>
</dependency>
```

## Usage

The example uses maven to build and run the microservices.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-opentracing
    $ mvn clean package
    ```
    
2. Run each individual microservice separately (separate terminal):
    
    ```bash
    $ java -jar customers/target/opentracing-customers-1.0.0-SNAPSHOT.jar
    $ java -jar orders/target/opentracing-orders-1.0.0-SNAPSHOT.jar
    ```
    
3. Navigate to <http://localhost:3000/v1/customers/1/orders>

4. View traces in [Jaeger console](http://localhost:16686) or [Zipkin console](http://localhost:9411)


## Tutorial

This tutorial will guide you through the steps required to create 2 simple 
REST microservices (customers and orders) and instrument them with distributed tracing.

We will follow these steps:
- Create 2 REST microservices (customers and orders)
- Add Maven dependencies for distributed tracing
- Tracing with no code instrumentation
- Tracing with explicit code instrumentation
- Build and run 

### Create 2 REST microservices (customers and orders)

Create 2 microservices - customers and orders as described in 
[JAX-RS tutorial](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs#tutorial).

### Add Maven dependencies for distributed tracing
Add Jaeger tracing dependency for each microservice:


```xml
<dependency>
    <groupId>com.kumuluz.ee.opentracing</groupId>
    <artifactId>kumuluzee-opentracing-jaeger</artifactId> 
    <version>${kumuluzee-opentracing.version}</version>
</dependency>
```

Add CDI dependency:

```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-cdi-weld</artifactId>
</dependency>
```

By now you should have the following dependencies in pom.xml in Customers and Orders microservice:
```xml
<dependencies>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
        <artifactId>kumuluzee-core</artifactId>
    </dependency>
    
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
        <artifactId>kumuluzee-cdi-weld</artifactId>
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
        <groupId>com.kumuluz.ee.opentracing</groupId>
        <artifactId>kumuluzee-opentracing-jaeger</artifactId>
        <version>${kumuluzee-opentracing.version}</version>
    </dependency>
</dependencies>
```

### Tracing with no code instrumentation
Create beans.xml file in resources/META-INF directory for each microservice with the following content:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/beans_1_2.xsd"
       bean-discovery-mode="annotated">
</beans>
```

Add config.yaml file in resources directory for each microservice. 
This step is optional. If config is not present extension will use default values as described [here](https://github.com/kumuluz/kumuluzee-opentracing).
```yaml
# Customers microservice config
kumuluzee:
  name: Customers Microservice
  server:
    http:
      port: 3000
  opentracing: # default config
    jaeger:
      agent-host: localhost
      agent-port: 5775
    zipkin:
      agent-host: http://localhost
      agent-port: 9411

mp: 
  opentracing:
    server:
      operation-name-provider: http-path
      skip-pattern: /openapi.*|/health.*
```

```yaml
# Orders microservice config
kumuluzee:
  name: Orders Microservice
  server:
    http:
      port: 3001
  opentracing: 
    jaeger:
      agent-host: localhost
      agent-port: 5775
    zipkin:
      agent-host: http://localhost
      agent-port: 9411

mp:
  opentracing:
    server:
      operation-name-provider: class-method # default operation name provider
      skip-pattern: /openapi.*|/health.*
```

At this point tracing with no code instrumentation should be working. 
You can test this by following steps as described in Usage chapter above.

### Tracing with explicit code instrumentation

We will add Customers bean in Customers microservice. 
Customers bean will implement method for retrieving customer orders from Orders microservice:

```java
@RequestScoped
public class CustomersBean {

    private Client httpClient;

    @Inject
    Tracer configuredTracer; // here we inject Tracer so we can append custom logs and tags to active span

    @PostConstruct
    private void init() {
        httpClient = ClientTracingRegistrar.configure(ClientBuilder.newBuilder()).build();  // enable client tracing
    }

    @Traced(operationName = "Get customer orders") // create new span for this method with custom span name
    public Response getOrders() {
        try {
            configuredTracer.activeSpan().log("Fetching customer orders..."); // add log to active span
            return httpClient.target("http://localhost:3001/v1/orders")
                    .request()
                    .get();
        } catch (WebApplicationException | ProcessingException e) {
            throw new WebApplicationException(e);
        }
    }
}
```

We will also add new method into CustomerResource class:

```java
@Inject
private CustomersBean customersBean;


@GET
@Path("{customerId}/orders")
public Response getCustomerOrders(@PathParam("customerId") String customerId) {
    return customersBean.getOrders();
}
```


In orders microservice we will add some code in OrderResource class:

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("orders")
@ApplicationScoped
public class OrderResource {

    @Inject
    Tracer configuredTracer;

    @GET
    @Traced(operationName = "GET all orders") // changed default span name
    public Response getAllOrders() {
        configuredTracer.activeSpan().log("Getting all orders..."); // add log to active span
        configuredTracer.activeSpan().setTag("test-tag", "Test tag value"); // add tag to active span 
        List<Order> orders = Database.getOrders();
        configuredTracer.activeSpan().log("Got "+ orders.size() + " orders.");
        return Response.ok(orders).build();
    }

    @GET
    @Path("{orderId}")
    public Response getOrder(@PathParam("orderId") String orderId) {
        Order order = Database.getOrder(orderId);
        return order != null
                ? Response.ok(order).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response addNewOrder(Order order) {
        Database.addOrder(order);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{orderId}")
    public Response deleteOrder(@PathParam("orderId") String orderId) {
        Database.deleteOrder(orderId);
        return Response.noContent().build();
    }
}
```

### Build and run
To build the microservice and run the example, use the commands as described in previous sections.