# KumuluzEE Rest Client sample

> Build a REST service, which consumes another REST service using the KumuluzEE Rest Client

The objective of this sample is to demonstrate the usage of the KumuluzEE Rest Client library to invoke RESTful services
in a type-safe way. Required knowledge: basic familiarity with JAX-RS and basic concepts of REST and JSON.

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

This sample requires a running instance of the
[KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs). The JAX-RS sample
requires no additional dependencies and is easy to run and understand.

## Usage

The example uses maven to build and run the microservices.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-rest-client
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
* JAX-RS REST resource - http://localhost:8081/v1/operations

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to use KumuluzEE Rest Client and pack the application as a
KumuluzEE microservice.

We will follow these steps:
* Add Maven dependencies
* Implement entities
* Implement API interface
* Implement REST interface which will consume created API
* Add configuration
* Add ResponseExceptionMapper
* Register ResponseExceptionMapper
* Build the microservice and run it

### Add Maven dependencies

We will need the following dependencies in our microservice:

- `kumuluzee-core`
- `kumuluzee-servlet-jetty`
- `kumuluzee-jax-rs-jersey`
- `kumuluzee-cdi-weld`
- `kumuluzee-rest-client`

To add them, add the following snippet to the pom.xml:

```xml
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
    <groupId>com.kumuluz.ee.rest-client</groupId>
    <artifactId>kumuluzee-rest-client</artifactId>
    <version>${kumuluzee-rest-client.version}</version>
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

### Implement entities

The JAX-RS microservice exposes customers in its RESTful API. In order to consume them in a type-safe way, we need to
define a `Customer` entity in our own microservice. The entity will be a simple POJO:

```java
public class Customer implements Serializable {

    private String id;
    private String firstName;
    private String lastName;

    // getters and setters
}
```

### Implement API interface

The API interface is the core of this sample, since it describes the API our microservice will consume. The interface
uses existing JAX-RS annotations, which you should already be familiar with. The methods declared in the interface are
one-to-one mapping to the methods in
[JAX-RS resource](https://github.com/kumuluz/kumuluzee-samples/blob/master/jax-rs/src/main/java/com/kumuluz/ee/samples/jaxrs/CustomerResource.java)
of the microservice, exposing the API.

```java
@Path("/customers")
@RegisterRestClient
@Dependent
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CustomerApi {

    @GET
    List<Customer> getAllCustomers();

    @GET
    @Path("{customerId}")
    Customer getCustomer(@PathParam("customerId") String id);

    @POST
    void createCustomer(Customer customer);

    @DELETE
    @Path("{customerId}")
    void deleteCustomer(@PathParam("customerId") String id);
}
```

The only new annotation is the `@RegisterRestClient` annotation. As we will see, you can retrieve a rest client
programmatically or with CDI. The `@RegisterRestClient` simply registers the interface as a bean and enables
the use of CDI lookup.

### Implement REST interface which will consume created API

In order to consume the created API, we will expose our own RESTful API.

__NOTE:__ You should put the application class and resources in a separate package. Otherwise, JAX-RS will try to
register the `CustomerApi` interface we created before and fail because it's an interface and not a concrete class.

First, create the application class:

```java
@ApplicationPath("v1")
public class RestApplication extends Application {
}
```

And then, the resource:

```java
@RequestScoped
@Path("operations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestResource {

    // TODO add methods
}
```

Now, let's add a simple method, that will proxy all customers from the JAX-RS microservice. We will use CDI lookup to
get an instance of the rest client and then proceed to use it in a method. Note that when using CDI lookup the
`@RestClient` qualifier must be used.

```java
@Inject
@RestClient
private CustomerApi customerApi;

@GET
public Response getAllCustomers() {
    return Response.ok(customerApi.getAllCustomers()).build();
}
```

We can also create more complex methods and use rest client in them. This one creates a batch of sample Customers:

```java
@GET
@Path("batch")
public Response createBatchCustomers() {
    String[] ids = {"1", "2", "3"};
    String[] firstNames = {"Jonh", "Mary", "Joe"};
    String[] lastNames = {"Doe", "McCallister", "Green"};

    for (int i = 0; i < ids.length; i++) {
        Customer c = new Customer();
        c.setId(ids[i]);
        c.setFirstName(firstNames[i]);
        c.setLastName(lastNames[i]);

        customerApi.createCustomer(c);
    }

    return Response.noContent().build();
}
```

Both methods use the CDI lookup to get an instance of the rest client. An alternative is the rest client builder, which
can be used in non-CDI environments and when a more complex configuration is required. Let's create another method to
showcase the builder usage:

```java
@GET
@Path("{cId}")
public Response getSingleMasked(@PathParam("cId") String customerId) throws MalformedURLException {

    CustomerApi programmaticLookupApi = RestClientBuilder.newBuilder()
            .baseUrl(new URL("http://localhost:8080/v1"))
            .build(CustomerApi.class);

    Customer c = programmaticLookupApi.getCustomer(customerId);
    c.setFirstName(c.getFirstName().substring(0, 1) + getStars(c.getFirstName().length() - 1));
    c.setLastName(c.getLastName().substring(0, 1) + getStars(c.getLastName().length() - 1));

    return Response.ok(c).build();
}

private String getStars(int len) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < len; i++) {
        s.append("*");
    }

    return s.toString();
}
```

This method retrieves a single customer and masks his first and last name, replacing all but first letter with `*`.

### Add configuration

Notice that we specified the URL of the API we will consume only when using programmatic lookup. For CDI lookup, we
need to specify the URL through configuration. We will also change the port of our microservice to `8081` since the API
we will consume is already running on port `8080`.

```yaml
kumuluzee:
  server:
    http:
      port: 8081

  rest-client:
    registrations:
      - class: com.kumuluz.ee.samples.kumuluzee_rest_client.api.CustomerApi
        url: http://localhost:8080/v1
```

The microservice is now ready to run. We will however showcase an additional feature in the next step.

### Add ResponseExceptionMapper

The `ResponseExceptionMapper` maps responses to exceptions, if applicable. For example, a default mapper is already
provided, which maps all statuses >=400 to a `WebApplicationException`. We will showcase a more advanced case: if a user
with id=1 is requested, we will throw an exception since the data is sensitive. First, let's create an exception:

```java
public class SensitiveDataException extends RuntimeException {
}
```

Now create the `ResponseExceptionMapper`:

```java
public class SensitiveDataResponseMapper implements ResponseExceptionMapper<SensitiveDataException> {

    @Override
    public SensitiveDataException toThrowable(Response response) {

        response.bufferEntity();

        try {
            Customer c = response.readEntity(Customer.class);

            if (c.getId().equals("1")) {
                return new SensitiveDataException();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    @Override
    public boolean handles(int status, MultivaluedMap<String, Object> headers) {
        return status == HttpServletResponse.SC_OK;
    }
}
```

In the `handles()` method, we check if the response return status is `200`. In the `toThrowable()` method, we try to
read the response body and check, if customer id equals to `1`. If true, we throw an instance of the
`SensitiveDataException`, otherwise we return `null` and invocation will proceed as expected.

### Register ResponseExceptionMapper

In order to register the created `ResponseExceptionMapper` (and any other providers, you create) to the rest clients
acquired with the CDI lookup, annotate the `CustomerApi` interface with the
`@RegisterProvider(SensitiveDataResponseMapper.class)` annotation.

The provider must also be registered when performing the programmatic lookup like so:

```java
CustomerApi programmaticLookupApi = RestClientBuilder.newBuilder()
        .baseUrl(new URL("http://localhost:8080/v1"))
        .register(SensitiveDataResponseMapper.class)
        .build(CustomerApi.class);
```

Notice the added `register(...)` call.

### Build the microservice and run it

First, make sure the JAX-RS microservice is running on port `8080`.

To build the microservice and run the example, use the commands as described in previous sections.
