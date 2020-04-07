# KumuluzEE OpenAPI MicroProfile sample

> Develop a microservice application with support for MicroProfile OpenAPI specification.

The objective of this sample is to demonstrate how to document API with OpenAPI MicroProfile compliant annotations. The
tutorial will guide you through the necessary steps. You will add KumuluzEE dependencies into pom.xml. 
To enable support for OpenAPI annotations you will use __kumuluzee-openapi-mp__ extension. 
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
    $ cd kumuluzee-openapi-mp
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

OpenAPI specification for API can be accessed on the following URL:
* API v2 - http://localhost:8080/openapi

Swagger UI can be accessed on the following URL:
* http://localhost:8080/openapi/ui

If you change the server context path you need to prepend the context path to default or custom mapping 
path of the specification and/or ui. 

## Tutorial

This tutorial will guide you through the steps required to document JAX-RS application using MicroProfile OpenAPI
annotations. 

Package contains the following JAX-RS application CustomerAPI.

**CustomerAPI v2**
JAX-RS resource:
* GET http://localhost:8080/v2/customer - list of all customers.

OpenAPI specification:
* GET http://localhost:8080/openapi or GET http://localhost:8080/openapi?format=json

By default api-specs will be generated and exposed on /openapi. The default format is YAML. You can change the format to
JSON by adding `?format=json` to the request.

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Microprofile OpenAPI)
* Implement the JAX-RS resource using standard JAX-RS API
* Use OpenAPI annotations to document APIs
* Build the microservice
* Configure OpenAPI extension behaviour
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

Add the `kumuluzee-openapi-mp` dependency:
```xml
<dependency>
    <groupId>com.kumuluz.ee.openapi</groupId>
    <artifactId>kumuluzee-openapi-mp</artifactId>
    <version>${kumuluzee-openapi-mp.version}</version>
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

KumuluzEE OpenAPI MicroProfile extension brings MicroProfile compliant annotations for documenting APIs.

#### Documenting CustomerAPI v2

##### Application class

```java
@SecurityScheme(securitySchemeName = "openid-connect", type = SecuritySchemeType.OPENIDCONNECT,
        openIdConnectUrl = "http://auth-server-url/.well-known/openid-configuration")
@ApplicationPath("v2")
@OpenAPIDefinition(info = @Info(title = "CustomerApi", version = "v2.0.0", contact = @Contact(), license = @License(name="something")), servers = @Server(url = "http://localhost:8080/v2"), security
        = @SecurityRequirement(name = "openid-connect"))
public class CustomerApplication extends Application {
}
```

##### Resources

```java
@Path("customer")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @GET
    @Operation(summary = "Get customers list", description = "Returns a list of customers.")
    @APIResponses({
            @APIResponse(description = "List of customers", responseCode = "200", content = @Content(schema = @Schema(implementation =
                    Customer.class, type = SchemaType.ARRAY)))
    })
    public Response getCustomers() {
        List<Customer> customers = new ArrayList<>();
        Customer c = new Customer("1", "John", "Doe");
        customers.add(c);
        return Response.status(Response.Status.OK).entity(customers).build();
    }

    @GET
    @Operation(summary = "Get customers details", description = "Returns customer details.")
    @APIResponses({
            @APIResponse(description = "Customer details", responseCode = "200", content = @Content(schema = @Schema(implementation =
                    Customer.class)))
    })
    @Path("{customerId}")
    public Response getCustomer(@PathParam("customerId") String customerId) {
        Customer c = new Customer("1", "John", "Doe");
        return Response.status(Response.Status.OK).entity(c).build();
    }

}
```

### OpenAPI configuration

Behaviour of the extension can be modified by using the KumuluzEE configuration framework. For example to limit package
scanning to only `com.kumuluz.ee.samples.openapi` you can use the following _config.yml_ file:

```yaml
kumuluzee:
  openapi-mp:
    scan:
      packages: com.kumuluz.ee.samples.openapi
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
