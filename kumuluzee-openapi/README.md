# KumuluzEE OpenAPI sample

> Develop a microservice application with support for OpenAPI specification.

The objective of this sample is to demonstrate how to document API with OpenAPI v3 compliant annotations. The tutorial will guide you through the necessary steps. You will add KumuluzEE dependencies into pom.xml. 
To enable support for OpenAPI annotations you will use kumuluzee-openapi extension. 
Required knowledge: basic familiarity with JAX-RS.

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
    $ cd kumuluzee-openapi
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
* JAX-RS - http://localhost:8080/v2/customer

### OpenAPI specification

OpenAPI specification for API can be access on the following URL:
* API v2 - http://localhost:8080/api-specs/v2/openapi.json

OpenAPI specification URL always follows the following URL template:
* http://<-hostname->:<-port->/api-specs/<jax-rs application-base-path>/openapi.[json|yaml]

## Tutorial

This tutorial will guide you through the steps required to document JAX-RS application using OpenAPI annotations. 

Package contains the following JAX-RS application CustomerAPI.

**CustomerAPI v2**
JAX-RS resource:
* GET http://localhost:8080/v2/customer - list of all customers.

OpenAPI specification:
* GET http://localhost:8080/api-specs/v2/openapi.json


We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (OpenAPI)
* Implement the JAX-RS resource using standard JAX-RS API
* Use OpenAPI annotations to document APIs
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

Add the `kumuluzee-openapi` dependency:
```xml
<dependency>
    <groupId>com.kumuluz.ee.openapi</groupId>
    <artifactId>kumuluzee-openapi</artifactId>
    <version>${kumuluzee-openapi.varsion}</version>
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

### Implement the service

Tutorial for the implementation of JAX-RS is described in **jax-rs** sample.
 
### Documenting API 

KumuluzEE-OpenAPI extension brings OpenAPI v3 compliant annotations for documenting APIs.

#### Documenting CustomerAPI v2

##### Application class

```java
@ApplicationPath("v2")
@OpenAPIDefinition(info = @Info(title = "CustomerApi", version = "v2.0.0"), servers = @Server(url = "http://localhost:8080/v2"))
public class CustomerApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(CustomerResource.class);

        return classes;
    }
}
```

##### Resources

```java
@Path("customer")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @GET
    @Operation(summary = "Get customers list", tags = {"customers"}, description = "Returns a list of customers.",
            responses = {
                    @ApiResponse(
                            description = "List of customers",
                            responseCode = "200",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class)))
                    )
            })
    public Response getCustomers() {

        List<Customer> customers = new ArrayList<>();
        Customer c = new Customer("1", "John", "Doe");

        customers.add(c);

        return Response.status(Response.Status.OK).entity(customers).build();
    }

    @GET
    @Operation(summary = "Get customers details", tags = {"customers"}, description = "Returns customer details.", responses = {
            @ApiResponse(description = "Customer details", responseCode = "200", content = @Content(schema = @Schema(implementation =
                    Customer.class)))
    })
    @Path("{customerId}")
    public Response getCustomer(@PathParam("customerId") String customerId) {

        Customer c = new Customer("1", "John", "Doe");

        return Response.status(Response.Status.OK).entity(c).build();
    }
}
```
 
### Configure OpenAPI extension
 
By default OpenAPI extension will automatically expose API specification, however this can be disabled by property **kumuluzee.openapi.spec.enabled** in config.

```yaml
kumuluzee:
  openapi:
    spec:
      enabled: false
```

#### OpenAPI UI

To enable OpenAPI UI add **kumuluzee-openapi-ui** dependency to your project: 

```xml
<dependency>
    <groupId>com.kumuluz.ee.openapi</groupId>
    <artifactId>kumuluzee-openapi-ui</artifactId>
    <version>${kumuluzee-openapi.varsion}</version>
</dependency>
```

In case you want to disable OpenAPI you can set the following property:

```yaml
kumuluzee:
  openapi:
    ui:
      enabled: false
```

**OpenAPI UI** is available at:
http://localhost:8080/api-specs/ui

If serving of the API specification is disabled, OpenAPI-UI will not be available.

### Runtime configuration
By default api-specs will be generated and exposed on /api-specs url. To disable openapi definirions and Swagger UI in runtime you can use configuration property **kumuluzee.openapi.enabled** and set it to *false* (example in **config.yaml**).


### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
