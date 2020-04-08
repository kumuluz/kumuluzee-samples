# KumuluzEE Fault Tolerance sample 

> Build a REST service which utilizes KumuluzEE Fault Tolerance to provide fault tolerance and latency tolerance to 
your code using the fault tolerance patterns in a KumuluzEE microservice

The objective of this sample is to show how to develop a microservice that uses KumuluzEE Fault Tolerance extension to
provide fault tolerance and latency tolerance. In this sample we will develop two simple REST services using microservice 
pattern. One microservice will be dependent on the other and will produce HTTP calls to second one using fault tolerant
patterns. We will demonstrate how to configure KumuluzEE Fault Tolerance using KumuluzEE config extension. 
Optionally you can use etcd configuration server for KumuluzEE Fault Tolerance configuration. This tutorial 
will guide you through all the necessary steps. To develop the REST services, we will use the standard JAX-RS 2 API. Required knowledge: 
basic familiarity with JAX-RS 2, fault tolerance patterns (circuit breaker, bulkhead, timeout, fallback) and basic 
concepts of REST. Optional knowledge: basic knowledge of KumuluzEE Config and KumuluzEE Metrics.

This sample contains two modules, each representing one microservice - customers and orders REST service. Customer can 
have multiple orders and therefore customer service uses orders service to get customer's orders. The third module represents
common library which contains models for customer and order with simple database simulation.

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

## Usage 

The example uses Maven to build and run the microservices.

1. Build the sample using Maven:

    ```bash
    $ cd kumuluzee-fault-tolerance
    $ mvn clean package
    ```

2. Run the sample customer-api:

- Uber-jar:

    ```bash
    $ java -jar customer-api/target/${project.build.finalName}.jar
    ```

    in Windows environment use the command
    ```
    java -jar customer-api/target/${project.build.finalName}.jar
    ```

- Exploded:

    ```bash
    $ java -cp customer-api/target/classes:customer-api/target/dependency/* com.kumuluz.ee.EeApplication
    ```

    in Windows environment use the command
    ```
    java -cp customer-api/target/classes;customer-api/target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
2. Run the sample order-api in separate terminal:

- Uber-jar:

    ```bash
    $ java -jar order-api/target/${project.build.finalName}.jar
    ```

    in Windows environment use the command
    ```
    java -jar order-api/target/${project.build.finalName}.jar
    ```

- Exploded:

    ```bash
    $ java -cp order-api/target/classes:order-api/target/dependency/* com.kumuluz.ee.EeApplication
    ```

    in Windows environment use the command
    ```
    java -cp order-api/target/classes;order-api/target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
The microservices can be accessed on the following URLs:
* JAX-RS REST resource for customers - http://localhost:8080/v1/customers
* JAX-RS REST resource for orders - http://localhost:8081/v1/orders

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to execute HTTP call to a remote REST microservice with 
fault tolerance patterns using a KumuluzEE Fault Tolerance. REST services will be based on existing 
 [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs). 

We will follow these steps:
* Complete the tutorial for [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs)
* Create a maven project with three submodules and add dependency management
* Create and implement common module with models and simple database simulation
* Create a REST microservice for orders
* Create a REST microservice for customers
* Add HTTP client dependency and HTTP client implementation
* Wrap HTTP call with fault tolerance patterns
* Test fault tolerance patterns and configurations
* Check out the fault tolerance metrics

### Create a maven project

Create a maven project with three submodules - common, order-api and customer-api. Add KumuluzEE BOM to root `pom.xml`:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-bom</artifactId>
            <version>${kumuluzee.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

        <dependency>
            <groupId>com.kumuluz.ee.samples</groupId>
            <artifactId>kumuluzee-fault-tolerance-common</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.kumuluz.ee.fault.tolerance</groupId>
            <artifactId>kumuluzee-fault-tolerance-smallrye</artifactId>
            <version>${kumuluzee-fault-tolerance.version}</version>
        </dependency>
        <dependency>
            <groupId>com.kumuluz.ee.config</groupId>
            <artifactId>kumuluzee-config-mp</artifactId>
            <version>${kumuluzee-config-mp.version}</version>
        </dependency>
        
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>${httpclient.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

We have already defined dependencies for Apache HttpComponents and Jackson Databind which we will be needing later on.

### Create and implement a common module

In common module we will add both models for orders and customers. `Customer` is a simple POJO Java class and is similar to one in KumuluzEE 
JAX-RS REST sample. We only added list for customer's orders:

```java
public class Customer {

    private String id;
    
    private String firstName;
    
    private String lastName;
    
    private List<Order> orders;
    
    // TODO: implement get and set methods
}
```

In the same way, implement the `Order` Java class:

```java
public class Order {

    private String id;
    
    private double price;
    
    private String paymentType;
    
    private String address;
    
    private String customerId;
    
    // TODO: implement get and set methods
}
```

Implementation of `Database` class for simple database simulation is also very similar to one in KumuluzEE JAX-RS 
REST sample. We added methods and list for `Order` model:

```java
public class Database {

    private static List<Order> orders = new ArrayList<>();

    public static void addOrder(String customerId) {

        for (Customer customer : customers) {
            if (customer.getId().equals(customerId)) {
                customers.remove(customer);
                break;
            }
        }
    }

    public static Order getOrder(String orderId) {

        for (Order order : orders) {
            if (order.getId().equals(orderId))
                return order;
        }

        return null;
    }

    public static void addOrder(Order order) {
        orders.add(order);
    }

    public static void deleteOrder(String orderId) {

        for (Order order : orders) {
            if (order.getId().equals(orderId)) {
                orders.remove(order);
                break;
            }
        }
    }

    public static List<Order> findOrdersByCustomer(String customerId) {

        return orders.stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }
    
    // List and methods for customers from KumuluzEE JAX-RS REST sample
}

```

### Create a REST microservice for orders

We will now create REST service for orders. Create a order-api module add the following dependencies to `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>com.kumuluz.ee.samples</groupId>
        <artifactId>kumuluzee-fault-tolerance-common</artifactId>
    </dependency>

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

Add the `Application` JAX-RS class, similar to one for customers in KumuluzEE JAX-RS REST sample. Implement the
resource class for orders API, which is also very similar to one for customers in KumuluzEE JAX-RS REST sample.
We will only modify the method for getting all orders in order to be also able to filter orders by customer's id:

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("orders")
public class OrderResource {

    @GET
    @PermitAll
    public Response getAllOrders(@QueryParam("customerId") String customerId) {

        List<Order> orders = customerId == null ?
                Database.getOrders() :
                Database.findOrdersByCustomer(customerId);

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
    public Response addOrder(Order order) {

        Database.addOrder(order);

        return Response.noContent().build();
    }

    @DELETE
    @Path("{orderId}")
    public Response removeOrder(@PathParam("orderId") String orderId) {

        Database.deleteOrder(orderId);

        return Response.noContent().build();
    }
}

```

Orders service is now set up. If you run the order-api KumuluzEE application, you should already be able to manage orders
through REST.

### Create a REST microservice for customers

A REST microservice for customers can be copied from KumuluzEE JAX-RS REST sample. We will have to modify the 
`pom.xml` in order to be able to use CDI:

```xml
<dependencies>
    <dependency>
        <groupId>com.kumuluz.ee.samples</groupId>
        <artifactId>kumuluzee-fault-tolerance-common</artifactId>
    </dependency>
    
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
    
    <dependency>
        <groupId>com.kumuluz.ee.fault.tolerance</groupId>
        <artifactId>kumuluzee-fault-tolerance-smallrye</artifactId>
    </dependency>
    <dependency>
        <groupId>com.kumuluz.ee.config</groupId>
        <artifactId>kumuluzee-config-mp</artifactId>
    </dependency>
</dependencies>
```

We have already added dependencies for KumuluzEE Fault Tolerance extension. The KumuluzEE Config MP dependency is
required in order for KumuluzEE Faule Tolerance to work correctly.

### Add HTTP client dependency and HTTP client implementation

In order to be able to call remote order REST service, we need to implement HTTP client. We will use 
[Apache HttpComponents](https://hc.apache.org) library. We also need a library to be able to parse JSON response to object. The 
easiest way to do so is with the [Jackson Databind](https://github.com/FasterXML/jackson-databind) library. 
Add the following dependencies to customer-api `pom.xml`: 

```xml
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

For executing HTTP calls with Apache HttpComponents library, we should first implement a `@Producer` for creating a pool
 of HTTP clients:
 
```java
public class HttpClientProducer {

    private static final int DEFAULT_POOL_MAX_CONNECTIONS = 5;

    @Produces
    @ApplicationScoped
    public HttpClient httpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, (certificate, authType) -> true)
                .build();

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        Registry socketFactorRegistry = RegistryBuilder.create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", socketFactory)
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactorRegistry);
        connectionManager.setMaxTotal(DEFAULT_POOL_MAX_CONNECTIONS);

        return HttpClients.custom()
                .setSSLContext(sslContext)
                .setConnectionManager(connectionManager)
                .build();
    }
}
```

HTTP client can now be injected into CDI. To call the remote API using HTTP, we must first add `OrderBean` class,
 annotated with `@RequestScoped` to make it a CDI. Next, we implement a method which will call orders REST service 
 to receive JSON response on customer's orders and map them to array of `Order` objects:

```java
@RequestScoped
public class OrdersBean {

    private static final Logger log = LoggerFactory.getLogger(OrdersBean.class);

    private static String ordersApiPath;

    @Inject
    private HttpClient httpClient;

    private ObjectMapper objectMapper;

    static {
        ordersApiPath = ConfigurationUtil.getInstance().get("orders-api.path")
                .orElse("http://localhost:8081/v1/order");

        log.info("Orders API path set to " + ordersApiPath);
    }

    public OrdersBean() {
        objectMapper = new ObjectMapper();
    }

    public List<Order> findOrdersByCustomerId(String customerId) {

        try {

            HttpGet request = new HttpGet(ordersApiPath + "?customerId=" + customerId);
            HttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();

                if (entity != null)
                    return toOrdersArray(EntityUtils.toString(entity));
            } else {
                String msg = "Remote server '" + ordersApiPath + "' failed with status " + status + ".";
                log.warn(msg);
                throw new InternalServerErrorException(msg);
            }

        } catch (IOException e) {
            String msg = e.getClass().getName() + " occured: " + e.getMessage();
            log.warn(msg);
            throw new InternalServerErrorException(msg);
        }

        return new ArrayList<>();
    }

    private List<Order> toOrdersArray(String json) throws IOException {

        return json == null ?
                new ArrayList<>() :
                objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));
    }
}
```

Orders service path is provided through KumuluzEE config. If no configuration for path is specified, a default
is used. Next step is to change our REST resource to be able to call our `OrderBean` CDI. For this purpose we will
 modify method for getting customer by id in our REST resource:

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
@Path("customers")
public class CustomerResource {

    @Inject
    private OrdersBean ordersBean;

    @GET
    @Path("{customerId}")
    public Response getCustomer(@PathParam("customerId") String customerId) {

        Customer customer = Database.getCustomer(customerId);

        if (customer == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        List<Order> customerOrders = ordersBean.findOrdersByCustomerId(customerId);
        customer.setOrders(customerOrders);

        return Response.ok(customer).build();
    }
}
  
```

Note that in order to be able to properly inject `OrdersBean`, we needed to add `@RequestScoped` annotation to
`CustomerResource` class. Our customer-api should now already work and be able to receive orders for customer.

### Wrap HTTP call with fault tolerance patterns

We will now add annotations to our `OrdersBean` in order to execute our remote HTTP call with fault tolerance patterns
using KumuluzEE Fault Tolerance extension. We will add `@Bulkhead` annotation on class to enable bulkhead pattern for
methods within class. It will limit the number of concurrent executions. `@CircuitBreaker` annotation must be added
to method for enabling circuit breaker pattern. `@Timeout` and `@Asynchronous` are also added to method. First one will
enable timeout pattern and the second will turn on thread bulkhead execution.

Note that we have to return a `Future` object since the method is annotated with `@Asynchronous`. Simply change the
return statements to `CompletableFuture.completedFuture(...)` and KumuluzEE Fault Tolerance will do the rest.
 
We will also implement fallback method which will return one order with customerId field set and "N/A" values 
set on other fields. Add `@Fallback` annotation:

```java
@RequestScoped
public class OrdersBean {

    @CircuitBreaker
    @Fallback(fallbackMethod = "findOrdersByCustomerIdFallback")
    @Timeout
    @Asynchronous
    @Bulkhead
    public Future<List<Order>> findOrdersByCustomerId(String customerId) {

        try {

            HttpGet request = new HttpGet(ordersApiPath + "?customerId=" + customerId);
            HttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();

                if (entity != null)
                    return CompletableFuture.completedFuture(toOrdersArray(EntityUtils.toString(entity)));
            } else {
                String msg = "Remote server '" + ordersApiPath + "' failed with status " + status + ".";
                log.warn(msg);
                throw new InternalServerErrorException(msg);
            }

        } catch (IOException e) {
            String msg = e.getClass().getName() + " occured: " + e.getMessage();
            log.warn(msg);
            throw new InternalServerErrorException(msg);
        }

        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    public Future<List<Order>> findOrdersByCustomerIdFallback(String customerId) {

        log.info("Fallback called for findOrdersByCustomerId.");

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setName("N/A");
        order.setPaymentType("N/A");
        order.setId("N/A");

        List<Order> orders = new ArrayList<>();
        orders.add(order);

        return CompletableFuture.completedFuture(orders);
    }

}
```

If you rebuild and run customer service, HTTP client should now already be executed within Hystrix framework. We
will provide some configuration for Hystrix framework using KumuluzEE Config in a `config.yml` file:

```yml
kumuluzee:
  env: dev

  fault-tolerance:
    bulkhead:
      value: 5

    annotation-overrides:
      - class: com.kumuluz.ee.samples.circuit_breaker_hystrix.customer.beans.OrdersBean
        method: findOrdersByCustomerId
        annotation: timeout
        parameters:
          value: 1500
      - class: com.kumuluz.ee.samples.circuit_breaker_hystrix.customer.beans.OrdersBean
        method: findOrdersByCustomerId
        annotation: circuit-breaker
        parameters:
          request-threshold: 10
          failure-ratio: 0.3
          delay: 3000

orders-api:
  path: http://localhost:8081/v1/orders
```

A global override is set for `@Bulkhead` annotations. Some parameters for the `@Timeout` and `@CircuitBreaker` are also
set.

### Test fault tolerance patterns and configurations

Test the fault tolerance patterns by executing requests on the customer service. Observe what happens if you shut down
the orders service and keep making requests on the customer service.

### Check out the fault tolerance metrics

The next thing to do is to enable metrics collection for the fault tolerance patterns. To enable metrics collection
simply add the following dependency to the customer service:

```xml
<dependency>
    <groupId>com.kumuluz.ee.metrics</groupId>
    <artifactId>kumuluzee-metrics-core</artifactId>
    <version>${kumuluzee-metrics.version}</version>
</dependency>
```

You can check out the metrics on the following URL: http://localhost:8080/metrics

Note that the metrics will be shown in the Prometheus format. If you want to read metrics in a more human readable
format use a request making software (e.g. Postman) and add the `Accept: application/json` header to the request.
