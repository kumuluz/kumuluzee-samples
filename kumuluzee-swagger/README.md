# KumuluzEE Swagger sample

> Develop a microservice application with support for Swagger specification.

The objective of this sample is to demonstrate how to document API with Swagger/OpenAPI v2 compliant annotations. The tutorial will guide you through the necessary steps. You will add KumuluzEE dependencies into pom.xml. 
To enable support for Swagger annotations you will use kumuluzee-swagger extension. 
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
    $ cd kumuluzee-swagger
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
* JAX-RS - http://localhost:8080/v1/customer

### Swagger specification

OpenAPI specification for APIs can be access on the following URL:
* API v1 - http://localhost:8080/api-specs/v1/swagger.json

Swagger specification URL always follows the following URL template:
* http://<-hostname->:<-port->/api-specs/<jax-rs application-base-path>/swagger.[json|yaml]

## Tutorial

This tutorial will guide you through the steps required to document JAX-RS application using Swagger annotations. 

Package contains two versions of JAX-RS application CustomersAPI.

**CustomersAPI v1**
JAX-RS resource:
* GET http://localhost:8080/v1/customer - list of all customers.

OpenAPI specification:
* GET http://localhost:8080/api-specs/v1/swagger.json


We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (OpenAPI)
* Implement the JAX-RS resource using standard JAX-RS API
* Use Swagger annotations to document API
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

Add the `kumuluzee-swagger` dependency:
```xml
<dependency>
    <groupId>com.kumuluz.ee.swagger</groupId>
    <artifactId>kumuluzee-swagger</artifactId>
    <version>${kumuluzee-swagger.varsion}</version>
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

KumuluzEE-Swagger extension brings Swagger compliant annotations for documenting APIs.

#### Documenting CustomersAPI v1

##### Application class

```java
@ApplicationPath("v1")
@SwaggerDefinition(info = @Info(title = "CustomersAPI", version = "v1.0.0"), host = "localhost:8080")
public class CustomerApplication extends Application {

}
```

##### Resources

```java
@Path("customer")
@Api
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @GET
    @ApiOperation(value = "Get customers list", tags = {"customers"}, notes = "Returns a list of customers.")
    @ApiResponses(value = {@ApiResponse(message = "List of customers", code = 200, response = Customer.class)})
    public Response getCustomers() {

        List<Customer> customers = new ArrayList<>();
        Customer c = new Customer("1", "John", "Doe");

        customers.add(c);

        return Response.status(Response.Status.OK).entity(customers).build();
    }
}
```
 
### Configure Swagger extension
 
By default Swagger extension will expose API specification, however this can be disabled by property **kumuluzee.swagger.spec.enabled** in config.

```yaml
kumuluzee:
  swagger:
    spec:
      enabled: false
```

#### Swagger-UI

By default Swagger-UI (visualization of specification) is not included. To enable Swagger-UI set **kumuluzee.swagger.ui.enabled** to true:

```yaml
kumuluzee:
  swagger:
    ui:
      enabled: true
```

**Swagger-UI** is accessible at:
http://localhost:8080/api-specs/ui

If serving of the API specification is disabled the Swagger-UI will not be available.

### Runtime configuration
By default api-specs will be generated and exposed on /api-specs url. To disable swagger definitions and UI in runtime you can use configuration property **kumuluzee.swagger.enabled** and set it to false (see example in **config.yaml**).


### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
