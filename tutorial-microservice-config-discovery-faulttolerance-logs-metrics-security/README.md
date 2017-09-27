# Cloud-native Java EE Microservices with KumuluzEE: REST service using config, discovery, security, metrics, logging and fault tolerance

A goal of this tutorial is to develop a cloud-native Java EE microservice application, using KumuluzEE microservice 
framework and KumuluzEE extensions. Application will consist of two microservices which will work together to provide wanted 
functionalities.  

We will develop a sample application for managing customers and their orders. Developed application will consist of 
two microservices; one for managing customer entities and one for managing order entities. We will implement all 
important concepts and functionalities that are essential in microservice architecture, such as dynamic 
configuration, service discovery, fault tolerance, centralized log collection, performance metrics collections and 
security mechanisms. 

We will use the following KumuluzEE extensions:
- KumuluzEE REST for implementation of filtering, sorting and pagination on REST resources,
- KumuluzEE Config for dynamic reconfiguration of microservices with the use of configuration servers, 
- KumuluzEE Discovery for service registration and service discovery, 
- KumuluzEE Fault Tolerance for improving the resilience of microservices,
- KumuluzEE Logs for advanced centralised logging, 
- KumuluzEE Metrics for collection of performance metrics, 
- KumuluzEE Security for securing developed REST endpoints.

First, we will create a maven project that will hold both our microservices. We will then implements both 
microservices and use KumuluzEE extensions to implement configuration, service discovery, fault tolerance, logging, 
metrics collection and security mechanisms.

Complete source code can be found in the git repository.  

## Create maven project

The root maven project will hold both developed microservices. Each microservice will be splitted into three modules; 
persistence, with JPA Entities and database access logic, business-logic, with CDI beans holding implementation of 
business logic, and api module, exposing business logic in form of RESTful services. The full structure should be as 
follows:

- kumuluzee-tutorial
    - customers
        - customers-api
        - customers-business-logic
        - customers-persistence
    - orders
        - orders-api
        - orders-business-logic
        - orders-persistence 

We will use `pom.xml` file of the root project (kumuluzee-tutorial) to define all properties and dependencies which 
will be used in other modules. It should look something like:

```xml
<properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

    <!-- other properties --> 

    <kumuluzee.version>2.4.1</kumuluzee.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-bom</artifactId>
            <version>${kumuluzee.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        
        <!-- other dependencies -->
        
    </dependencies>
</dependencyManagement>
```

## Customer microservice

First, we will implement customer service that will provide CRUD functionalities for custumer objects. 

### Maven dependencies

Before we start writing code, we must first add all Maven dependencies that we will need in this microservice.

#### Persistence module

In the persistence module we will need JPA dependency for accessing the database. We will use postgresql database, 
hence we also need postgresql JDBC driver.

```xml
<dependencies>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
        <artifactId>kumuluzee-jpa-eclipselink</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
</dependencies>
```

#### Business-logic module 

Business logic module will implement business logic in CDI beans, hence we need to add a CDI implementation, which is 
available in `kumuluzee-cdi-weld` module. We will also need JPA entities and DB access logic, defined in our 
`persistence` module.

```xml
<dependencies>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
        <artifactId>kumuluzee-cdi-weld</artifactId>
    </dependency>
    <dependency>
        <groupId>com.kumuluz.ee.samples.tutorial</groupId>
        <artifactId>persistence</artifactId>
    </dependency>
</dependencies>
```

#### Api module

Api module will be the core module of our microservice that will be executed, hence it needs `kumuluzee-core` 
and `kumuluzee-servlet-jetty` modules. It will expose business logic as RESTful services, hence the `business-logic` 
and `kumuluzee-jax-rs-jersey` modules. We will also add `kumuluzee-maven-plugin` to package our microservice in a 
uber JAR.

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
        <groupId>com.kumuluz.ee.samples.tutorial</groupId>
        <artifactId>business-logic</artifactId>
    </dependency>
</dependencies>

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

### Develop a microservice

Now it is time to implement our microservice.

#### Add config

First, we will add basic KumuluzEE configuration in a `config.yml` configuration file. You can read more about 
KumuluzEE configuration framework [here](https://github.com/kumuluz/kumuluzee/wiki/Configuration). In our case, we 
will specify service name, version, environment, in which microservice is deployed, and set the server http port and 
base-url. 

```bash
kumuluzee:
  name: customer-service
  env:
    name: dev
  version: 1.0.0
  server:
    base-url: http://localhost:8080
    http:
      port: 8080
```

#### Develop a persistence module

Before we implement a persistance module, we have to run a database instance. We will use docker to achieve that. To 
run a new postgresql instance in docker, use the following command.  

```bash
docker run -d --name postgres-customers -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=customer -p 5432:5432 postgres:latest
```

##### Create entity

In this step, we will define a JPA entity that will be used to represent customers. We create the following class:

```bash
@Entity(name = "customer")
@NamedQueries(value =
        {
                @NamedQuery(name = "Customer.getAll", query = "SELECT c FROM customer c")
        })
@UuidGenerator(name = "idGenerator")
public class Customer {

    @Id
    @GeneratedValue(generator = "idGenerator")
    private String id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String address;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    // getter and setter methods
    
}
```

##### Define JDBC datasource in config 

A JDBC datasource have to be defined with KumuluzeEE configuration framework. We specify datasource name and database 
connection properties with the following configuration keys:

```bash
kumuluzee:
  datasources:
    - jndi-name: jdbc/CustomersDS
      connection-url: jdbc:postgresql://localhost:5432/customer
      username: postgres
      password: postgres
      max-pool-size: 20
```

##### Define persistance.xml

In order for our application to connect to defined datasource, we have to specify a persistence unit in 
`persistence.xml`. The following configuration will automatically execute a SQL script on microservice startup to 
populate database with development data. Such configuration is useful for development purposes, but not for 
production environments. 

```xml
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.1">
    <persistence-unit name="customers-jpa" transaction-type="RESOURCE_LOCAL">
        <non-jta-data-source>jdbc/CustomersDS</non-jta-data-source>

        <class>com.kumuluz.ee.samples.tutorial.customers.Customer</class>

        <properties>
            <property name="javax.persistence.schema-generation.database.action" value="drop-and-create"/>
            <property name="javax.persistence.schema-generation.create-source" value="metadata"/>
            <property name="javax.persistence.sql-load-script-source"
                      value="sql-scripts/init-customers.sql" />
            <property name="javax.persistence.schema-generation.drop-source" value="metadata"/>
        </properties>
    </persistence-unit>
</persistence>
```

#### Develop business logic module 

Business logic module will implement CRUD operations for managing customer entities. It will be implemented as an 
application scoped CDI bean. A `beans.xml` file have to be placed in `resources/META-INF` folder in order to enable CDI. 
A CDI bean with business logic should look something like:

```java
@RequestScoped
public class CustomersBean {

    @PersistenceContext(unitName = "customers-jpa")
    private EntityManager em;

    public List<Customer> getCustomers(){

        Query query = em.createNamedQuery("Customer.getAll", Customer.class);

        return query.getResultList();

    }
    
    public Customer getCustomer(String customerId) {
    
            Customer customer = em.find(Customer.class, customerId);
    
            if (customer == null) {
                throw new NotFoundException();
            }
    
            return customer;
        }
    
        public Customer createCustomer(Customer customer) {
    
            try {
                beginTx();
                em.persist(customer);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
    
            return customer;
        }
    
        public Customer putCustomer(String customerId, Customer customer) {
    
            Customer c = em.find(Customer.class, customerId);
    
            if (c == null) {
                return null;
            }
    
            try {
                beginTx();
                customer.setId(c.getId());
                customer = em.merge(customer);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
    
            return customer;
        }
    
        public boolean deleteCustomer(String customerId) {
    
            Customer customer = em.find(Customer.class, customerId);
    
            if (customer != null) {
                try {
                    beginTx();
                    em.remove(customer);
                    commitTx();
                } catch (Exception e) {
                    rollbackTx();
                }
            } else
                return false;
    
            return true;
        }

}
```

#### Develop API module

An API module will expose business logic as a set of RESTful services. First, add `beans.xml` file to 
`resources/META-INF` in order to enable CDI. 

##### Application class

To enable JAX-RS, we first add a class that extends `javax.ws.rs.core.Application` and annotate it with 
`@ApplicationPath`.

```java
@ApplicationPath("/v1")
public class CustomerApplication extends Application {
}
```

##### JAX-RS resource 

In the next step, we add a resource class that will expose business logic. It should look like:

```java
@RequestScoped
@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomersResource {

    @Inject
    private CustomersBean customersBean;

    @GET
    public Response getCustomers() {

        List<Customer> customers = customersBean.getCustomers();

        return Response.ok(customers).build();
    }
    
    @GET
    @Path("/{customerId}")
    public Response getCustomer(@PathParam("customerId") String customerId) {

        Customer customer = customersBean.getCustomer(customerId);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(customer).build();
    }

    @POST
    public Response createCustomer(Customer customer) {

        if ((customer.getFirstName() == null || customer.getFirstName().isEmpty()) || (customer.getLastName() == null
                || customer.getLastName().isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            customer = customersBean.createCustomer(customer);
        }

        if (customer.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(customer).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(customer).build();
        }
    }

    @PUT
    @Path("{customerId}")
    public Response putZavarovanec(@PathParam("customerId") String customerId, Customer customer) {

        customer = customersBean.putCustomer(customerId, customer);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (customer.getId() != null)
                return Response.status(Response.Status.OK).entity(customer).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @DELETE
    @Path("{customerId}")
    public Response deleteCustomer(@PathParam("customerId") String customerId) {

        boolean deleted = customersBean.deleteCustomer(customerId);
    
        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
```

##### KumuluzEE Rest

Now it is time to add our first KumuluzEE extension. We will use KumuluzEE Rest to add best practices of developing 
RESTful services, such as sorting, filtering and pagination. 

First, we add a maven dependency:

```xml
<dependency>
    <groupId>com.kumuluz.ee.rest</groupId>
    <artifactId>kumuluzee-rest-core</artifactId>
    <version>1.1.0</version>
</dependency>
```

Then, we inject `UriInfo` object into REST resource. `UriInfo` holds the data about the request's URL and will we used as
an input to KumuluzEE REST extension. We inject is as:

```java
@Context
protected UriInfo uriInfo;
```

We add a new REST endpoint which will be used to get filtered, sorted or paginated requests:

```java
@GET
@Path("/filtered")
public Response getCustomersFiltered() {

    List<Customer> customers;

    customers = customersBean.getCustomersFilter(uriInfo);

    return Response.status(Response.Status.OK).entity(customers).build();
}
```

We also add a method to the CDI bean. It uses `JPAUtils` object to query filtered entities:

```java
public List<Customer> getCustomersFilter(UriInfo uriInfo) {

    QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
            .build();

    List<Customer> customers = JPAUtils.queryEntities(em, Customer.class, queryParameters);

    return customers;
}
```

###### Test

We can test new endpoint with the following URLs.

Pagination: 
- localhost:8080/v1/customers/filtered?offset=1&limit=1

Sorting:
- localhost:8080/v1/customers/filtered?order=dateOfBirth DESC

Filtering:
- localhost:8080/v1/customers/filtered?filter=firstName:EQ:James
- localhost:8080/v1/customers/filtered?filter=firstName:NEQ:James
- localhost:8080/v1/customers/filtered?filter=lastName:LIKE:S%
- localhost:8080/v1/customers/filtered?where=dateOfBirth:GT:'2010-01-01T00:00:00%2B00:00'

##### Additional JAX-RS features

In this section we will add some additional JAX-RS features to our microservice.

###### Configure Jackson serializer

A Jackson serializer will be used to correctly display dates in our microservice. We implement is as a provider class
 that extends `javax.ws.rs.ext.ContextResolver`:

```java
@Provider
public class JacksonProducer implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper;

    public JacksonProducer() {

        mapper = new ObjectMapper();

        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        mapper.setDateFormat(dateFormat);
    }

    @Override
    public ObjectMapper getContext(Class<?> aClass) {
        return mapper;
    }
}
```

###### Exception mapper 

An exception mapper will be used to map errors into readable error messages. First, we define a DTO that will hold 
the error message: 

```java
public class Error {

    private Integer status;
    private String code;
    private String message;

    // getter and setter methods 

}
```

Then we add a mapper that will wrap NotFoundExceptions into newly defined Error objects:

```java
@Provider
@ApplicationScoped
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException e) {

        Error error = new Error();
        error.setStatus(404);
        error.setCode("resource.not.found");
        error.setMessage(e.getMessage());

        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(error)
                .build();
    }
}
```

### Run and test the microservice

We have two options for running our microservice:

1. We can use an IDE of our choice to run our microservice. We simply run it as Java application, with main class set 
to `com.kumuluz.ee.EeApplication`.

2. We can use maven to build it and then use java to run the jar.

```bash
mvn clean package
```

```bash
java -jar target/customers-api-1.0.0-SNAPSHOT.jar
```

We can test our microservice by accessing the following URL: http://localhost:8080/v1/customers

### Package microservice as docker image and run it

Now, it is time to package our microservice as a docker image and run it as a docker container. First, we will 
specify a dockerfile with information on image-building process:

```yaml
FROM openjdk:8-jre-alpine

RUN mkdir /app

WORKDIR /app

ADD ./customers-api/target/customers-api-1.0.0-SNAPSHOT.jar /app

EXPOSE 8080

CMD ["java", "-jar", "customers-api-1.0.0-SNAPSHOT.jar"]
```

To create docker image, perform the folowig steps:
- build microservice with `mvn clean package`.
- build docker image with `docker build -t customers-api:1.00 .`.

To run a docker container from the built image run: `docker run -e 
KUMULUZEE_DATASOURCES0_CONNECTIONURL=jdbc:postgresql://databaseUrl:5432/customer -p 8080:8080 customers-api:1.00`

#### Docker compose

Instead of running postgresql and microservice seperately, we could package them as a docker compose application with
 he following configuration:

```yaml
version: "3"
services:
  postgres:
    image: postgres:latest
    environment:
      - POSTGRES_PASSWORD=postgres
      - POSTGRES_DB=customer
    ports:
      - "5432:5432"
  customer-service:
    image: customers-api:1.00
    environment:
      - KUMULUZEE_DATASOURCES0_CONNECTIONURL=jdbc:postgresql://postgres:5432/customer
    ports:
      - "8080:8080"
    depends_on:
      - postgres
```


## Order microservice

Now we will develop a second microservice that will be used for managing data about orders. Each order will be 
related to one customer. 

### Develop a microservice

To develop an order microservice, we have to repeat similar steps as with customer microservice. 

First, we run annother postgresql instance for storing order data: 

```bash
docker run -d --name postgres-orders -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=order -p 5433:5432 postgres:latest
```

Then we define an `Order` JPA entity. We extend `Customer` entity with data about its orders: 

```java
@Transient
private List<Order> orders;
```

Since the orders field is annotated with `@Transient`, orders will not get fetched and stored into the database by JPA. 
Instead, we will retrieve them from the order microservice.

All the other steps of developing an order microservice are very similar as in customer service and will not be 
repeated here.

## Connect the two microservices 

We will now extend the customer microservice so that it will return orders of each queried customer. We will extend 
the business logic CDI bean with remote http call to order service. We have to add the following fields and methods:

```java
private ObjectMapper objectMapper;

private HttpClient httpClient;

private String basePath;

@Inject
private CustomersBean customersBean;

@PostConstruct
private void init() {
    httpClient = HttpClientBuilder.create().build();
    objectMapper = new ObjectMapper();

    basePath = "http://localhost:8081/v1/";
}

public Customer getCustomer(String customerId) {

    Customer customer = em.find(Customer.class, customerId);

    if (customer == null) {
        throw new NotFoundException();
    }

    List<Order> orders = customersBean.getOrders(customerId);
    customer.setOrders(orders);

    return customer;
}

public List<Order> getOrders(String customerId) {

    try {
        HttpGet request = new HttpGet(basePath + "/v1/orders?where=customerId:EQ:" + customerId);
        HttpResponse response = httpClient.execute(request);

        int status = response.getStatusLine().getStatusCode();

        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();

            if (entity != null)
                return getObjects(EntityUtils.toString(entity));
        } else {
            String msg = "Remote server '" + basePath + "' is responded with status " + status + ".";
            log.error(msg);
            throw new InternalServerErrorException(msg);
        }

    } catch (IOException e) {
        String msg = e.getClass().getName() + " occured: " + e.getMessage();
        log.error(msg);
        throw new InternalServerErrorException(msg);
    }
    return new ArrayList<>();

}
```

We can test the newly developed feature by accessing the following URL: localhost:8080/v1/customers/1 It should 
return a customer object with two orders.

## KumuluzEE Config

We will now add an option to disable remote calls to order service using KumuluzEE configuration framework and 
KumuluzEE Config to integrate configuration servers. First, we add a configuration key into `config.yml` file:

```yalm
rest-properties:
  external-dependencies:
    order-service:
      enabled: true
```

In the next step we add a properties bean that will load, hold and update configuration properties at runtime.

```java
@ApplicationScoped
@ConfigBundle("rest-properties")
public class RestProperties {

    @ConfigValue(value = "external-dependencies.order-service.enabled", watch = true)
    private boolean orderServiceEnabled;

    // getter and setter methods
}
```

We add an if statement to a `CustomerBean` class:

```java
if (restProperties.isOrderServiceEnabled()) {
    List<Order> orders = customersBean.getOrders(customerId);
    customer.setOrders(orders);
}
```

### Configuration server

Now it is time to add a configuration server. First, we add a KumuluzEE Config extension that will add configuration 
server as one of the available configuration sources:

```bash
<dependency>
    <groupId>com.kumuluz.ee.config</groupId>
    <artifactId>kumuluzee-config-etcd</artifactId>
    <version>${kumuluzee-config.version}</version>
</dependency>
```

We will use etcd as configuration server. We could replace it with Consul by just replacing the Maven dependency to 
`kumuluzee-config-consul`.

We can now run etcd server instance with the following docker command:

```bash
$ docker run -d -p 2379:2379 \
    --name etcd \
    --volume=/tmp/etcd-data:/etcd-data \
    quay.io/coreos/etcd:latest \
    /usr/local/bin/etcd \
    --name my-etcd-1 \
    --data-dir /etcd-data \
    --listen-client-urls http://0.0.0.0:2379 \
    --advertise-client-urls http://0.0.0.0:2379 \
    --listen-peer-urls http://0.0.0.0:2380 \
    --initial-advertise-peer-urls http://0.0.0.0:2380 \
    --initial-cluster my-etcd-1=http://0.0.0.0:2380 \
    --initial-cluster-token my-etcd-token \
    --initial-cluster-state new \
    --auto-compaction-retention 1 \
    -cors="*"
```

We can edit the values inside etcd with the following [editor](henszey.github.io/etcd-browser/).

Before we can access configuration server, we have to provide access configuration in `config.yml` file:

```yaml
config:
  etcd:
    hosts: http://192.168.99.100:2379
```

We can now override the configuration from configuration file and disable external dependency calls by setting the 
following etcd key to `false`: 
- /environments/dev/services/customer-service/1.0.0/config/rest-properties/external-dependencies/order-service/enabled

## KumuluzEE Discovery

In this step, we will add KumuluzEE Discovery extension to enable service registration and discovery instead of 
manually wiring the microservice. We will register the order microservice and use service discovery in the customers 
microservice.

Add the following dependency:

```xml
<dependency>
    <groupId>com.kumuluz.ee.discovery</groupId>
    <artifactId>kumuluzee-discovery-etcd</artifactId>
    <version>${kumuluzee-discovery.version}</version>
</dependency>
```

We have to provide configuration to access the etcd server:

```yaml
discovery:
  etcd:
    hosts: http://192.168.99.100:2379
```

### Register service

To register the order service, add the `@RegisterService` to the Application class:

```java
@ApplicationPath("/v1")
@RegisterService
public class OrdersApplication extends Application {
}
```

### Discover service

We will use service discovery in `CustomerBean` to get the URL of the registered order service. We retrieve service 
URL with simple injection:

```java
@Inject
@DiscoverService(value = "order-service", environment = "dev", version = "*")
private String basePath;
```

We can now remove manual wiring from the `init()` method.

We could also use Consul instead of etcd by simply changing Maven dependency to `kumuluzee-discovery-consul`.

## Fault tolerance

To achieve high resilience of our microservice application, we have to provide adequate fault tolerance mechanisms. 
We will use KumuluzEE Fault Tolerance extension. First, add the following Maven dependency: 

```xml
<dependency>
    <groupId>com.kumuluz.ee.fault.tolerance</groupId>
    <artifactId>kumuluzee-fault-tolerance-hystrix</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Adding fallback mechanisms

The most critical point of failure in our application is the communication between microservices. We do not want the 
customer microservice to be unavailable if order service fails. To achieve that, we will add fault tolerance 
mechanisms to the `getOrders` method:  

```java
@RequestScoped
@GroupKey("orders")
public class CustomersBean {
    
    ...

    @CircuitBreaker(failureRatio = 0.3)
    @Fallback(fallbackMethod = "getOrdersFallback")
    @CommandKey("http-get-order")
    @Timeout(value = 500)
    public List<Order> getOrders(String customerId) {
    
    }
    
    public List<Order> getOrdersFallback(String customerId) {
        return new ArrayList<>();
    }
    

}
```

We enabled fault tolerance with annotation `@GroupKey` on the class. We added annotations on the `getOrders` method. 
Annotation `@CircuitBreaker` opens circuit breaker if the request rate is higher than 30%. `@Timeout` prevents the 
method to wait for the response longer than 500 ms. With `@Fallback` we defined a method that will be called in case 
errors occcur. 


## Logging

In microservice architecture, logs are collected in the central log management system. We will use KumuluzEE Logs to 
enable advanced logging mechanisms and send logs to Logstash. First, add the following Maven dependency:

```xml
<dependency>
    <groupId>com.kumuluz.ee.logs</groupId>
    <artifactId>kumuluzee-logs-log4j2</artifactId>
    <version>${kumuluzee-logs.version}</version>
</dependency>
```

### Configure Log4j2

Add the `log4j2` configuration in the configuration file:

```yaml
kumuluzee:
    logs:
        config-file:
          '<?xml version="1.0" encoding="UTF-8"?>
           <Configuration name="tutorial-logging">
               <Appenders>
                   <Console name="console" target="SYSTEM_OUT">
                       <PatternLayout pattern="%d %p %marker %m %X %ex %n"/>
                   </Console>
    
                   <!-- A socket definition for sending logs into Logstash using TCP and JSON format.-->
                   <!--<Socket name="logstash" host="192.168.99.100" port="5043" protocol="tcp">
                      <JSONLayout complete="false" compact="true" eventEol="true" charset="UTF-8" properties="true"/>
                   </Socket>-->
    
               </Appenders>
               <Loggers>
                   <!-- Default logger -->
                   <Root level="trace">
                       <AppenderRef ref="console"/>
                       <AppenderRef ref="logstash"/>
                   </Root>
               </Loggers>
           </Configuration>'
```

This configuration outputs logs to the console and to the Logstash instance on specified address.

### Log endpoint calls

To enable automatic logging of all REST endpoint calls, add the `@Log` annotation to the `CustomerResource` class.

To log additional context parameters, such as microservice name, version and environment, you can implement the 
interceptor that will inject the data to the logging system:

```java
@Log
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE)
public class LogContextInterceptor {

    @AroundInvoke
    public Object logMethodEntryAndExit(InvocationContext context) throws Exception {

        ConfigurationUtil configurationUtil = ConfigurationUtil.getInstance();

        HashMap settings = new HashMap();

        settings.put("environmentType", configurationUtil.get("kumuluzee.env.name").orElse(null));
        settings.put("applicationName", configurationUtil.get("kumuluzee.name").orElse(null));
        settings.put("applicationVersion", configurationUtil.get("kumuluzee.version").orElse(null));
        settings.put("uniqueInstanceId", EeRuntime.getInstance().getInstanceId());

        settings.put("uniqueRequestId", UUID.randomUUID().toString());

        try (final CloseableThreadContext.Instance ctc = CloseableThreadContext.putAll(settings)) {
            Object result = context.proceed();
            return result;
        }
    }
}
```

All REST call are now logged in the following format: 

```
TRACE ENTRY[ METHOD ] Entering method. {applicationName=customer-service, applicationVersion=1.0.0, class=com.kumuluz.ee.samples.tutorial.customers.api.v1.resources.CustomersResource, environmentType=dev, method=getCustomer, parameters=[1], uniqueInstanceId=4da94ff8-f9ad-4702-a84a-aecd6cb15abf, uniqueRequestId=0db2128b-1887-46e2-bf0f-15c4c43e73c2}
```

## Metrics

If we want to monitor the performance of our microservices, we can add KumuluzEE Metrics extension: 

```xml
<dependency>
    <groupId>com.kumuluz.ee.metrics</groupId>
    <artifactId>kumuluzee-metrics-core</artifactId>
    <version>${kumuluzee-metrics.version}</version>
</dependency>
```

It collects the performance metrics of JVM, http calls to specified endpoints and other user-defined metrics. Collected 
metrics are available on localhost:8080/metrics

### Web instrumentation

To enable monitoring of REST calls on customer endpoint, we add the following configuraion:

```yaml
metrics:
  web-instrumentation:
    - name: customers-endpoint
      url-pattern: /v1/customers/*
      registry-name: customersRegistry
```

### Custom metrics

We can monitor the number of deleted customers by annotating the `deleteCustomer` method with 
`@Metered(name = "delete-requests")`.

## Security

We can restrict the access to REST endpoint with KumuluzEE Security extension. To include it, add the following 
dependency: 

```xml
<dependency>
    <groupId>com.kumuluz.ee.security</groupId>
    <artifactId>kumuluzee-security-keycloak</artifactId>
    <version>${kumuluzee-security.version}</version>
</dependency>
```

To start and configure a Keycloak instance follow the tutorial on [KumuluzEE Security sample]
(https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-security-keycloak#set-up-keycloack).

Add the keycloak configuration into configuration file:

```yaml
kumuluzee:
  security:
      keycloak:
        json: '{"realm": "customers-realm",
                "bearer-only": true,
                "auth-server-url": "http://localhost:8080/auth",
                "ssl-required": "external",
                "resource": "customers-api"}'
```


### Implement security

First we have to enable the security using the `@DeclareRoles` annotation on the main application class of the REST 
service:

```java
@DeclareRoles({"user", "admin"})
@ApplicationPath("v1")
public class CustomerApplication extends Application {
}
```

To restrict the access on the selected REST endpoint, use the `@RolesAllowed ` annotation.

```java
@DELETE
@Path("{customerId}")
@RolesAllowed("admin")
@Metered(name = "delete-requests")
public Response deleteCustomer(@PathParam("customerId") String customerId)
```

To get access token, that you will then use for accessing secured endpoints, follow the [KumuluzEE Security sample]
(https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-security-keycloak#test-security).

## Conclusion

In this tutorial we used KumuluzEE framework to build a microservice application composed of two microservices. We 
used KumuluzEE extensions to provide functionalities for configuration, service discovery, fault tolerance, logging, 
metrics collection and security. Complete source code can be found in the git repository.  
