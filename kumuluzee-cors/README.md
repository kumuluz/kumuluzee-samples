# KumuluzEE CORS sample

> Develop a microservice application with suport fot Cross Origin.

The objective of this sample is to show how to develop a servlet or JAX-RS application with support for Cross Origin Access. The tutorial will guide you through the necessary steps. You will add KumuluzEE dependencies into pom.xml. To enable support for CORS you will use kumuluzee-cors extension. 
Required knowledge: basic familiarity with servlets and JAX-RS.

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
    $ cd servlet
    $ mvn clean package
    ```

2. Run the sample:
* Uber-jar:

    ```bash
        $ java -jar target/${project.build.finalName}.jar.jar
    ```
    
    in Windows environemnt use the command
    ```batch
        java -jar target/${project.build.finalName}.jar.jar
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
* Servlet - http://localhost:8080/CustomerServlet
* JAX-RS - http://localhost:8080/v1/customer

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a simple microservice with support for Cross Origin access and pack it as Uber-jar. 

We will develope a simple microservice with JAX-RS resource and servlet.

JAX-RS resource:
* GET http://localhost:8080/v1/customer - list of all customers

Servlet:
* GET http://localhost:8080/CustomerServlet - list of all customers

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet, Cors)
* Implement the JAX-RS resource using standard JAX-RS API
* Implement the servlet using standard Servlet 3.1 API
* Use CORS annotations and provide CORS configuration to enable Cross origin support
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

Add the `kumuluzee-core` and `kumuluzee-servlet-jetty` dependencies:
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
</dependencies>
```

Add the `kumuluzee-cors` dependency:
```xml
<dependency>
    <groupId>com.kumuluz.ee.cors</groupId>
    <artifactId>kumuluzee-cors</artifactId>
    <version>${kumuluzee-cors.varsion}</version>
</dependency>
```

Add the `kumuluzee-maven-plugin` build plugin to package microservice as exploded:

```xml
<build>
    <plugins>
        <plugin>
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
        </plugin>
    </plugins>
</build>
```

### Implement the service

Register your module as JAX-RS service and define the application path. You could do that in web.xml or for example with `@ApplicationPath` annotation:

```java
@ApplicationPath("v1")
public class CustomerApplication extends Application {
}
```

Implement JAX-RS resource, for example, to implement resource `customers` which will return all customers by default on GET request:

```java
@Path("customer")
@Produces(MediaType.APPLICATION_JSON)
@CrossOrigin(allowOrigin = "http://resource-origin.com")
public class CustomerResource {

    @GET
    public Response getCustomers() {

        List<Customer> customers = new ArrayList<>();
        Customer c = new Customer("1", "John", "Doe");

        customers.add(c);

        return Response.status(Response.Status.OK).entity(customers).build();
    }
}
```

Implement the `Customer` Java class, which is a POJO:
```java
public class Customer {

    private String id;

    private String firstName;

    private String lastName;

    // TODO: implement get and set methods
}
```

### Implement the servlet

Implement the servlet, for example, which will return all customers by default on GET request:

```java
@WebServlet("CustomerServlet")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         List<Customer> customers = new ArrayList<>();
                Customer c = new Customer("1", "John", "Doe");
        
                customers.add(c);
        
                resp.getWriter().println(customers.toString());
    }
}
```

### Enable Cross Origin support on JAX-RS application

To enable Cross Origin support on JAX-RS application we use `@CrossOrigin` annotation. Annotation can be used on the level of:
* JAX-RS Application - annotation is provided on JAX-RS Application class. Configuration of annotation is inherited by all resources and methods.
* Resource - annotation is provided on JAX-RS Resource annotated with `@Path`. Configuration is inherited by all methods of resource.
* Method - annotation is provided on `HTTP method`.

In our sample we will enable Cross Origin on the level of resource:
```java
@Path("customer")
@Produces(MediaType.APPLICATION_JSON)
@CrossOrigin(allowOrigin = "http://resource-origin.com")
public class CustomerResource { ... }
```

Annotation contains default configuration which can be overwritten by setting properties of annotation or by providing config.yaml and naming the annotation as shown in the example of servlet.
 
### Enable Cross Origin support on Servlet

Cross Origin support is enabled by defining `@CrossOrigin` annotation on Servlet annotated with `@WebServlet`:
 ```java
@WebServlet("CustomerServlet")
@CrossOrigin(name = "customer-servlet")
public class CustomerServlet extends HttpServlet { ... }
```

In this case Cross Origin configuration is not provided by annotation but it is defined in confg.yaml:
 ```yaml
kumuluzee:
  cors-filter:
    annotations:
      customer-servlet:
        allow-origin: "http://origin2.kumuluz.com"
        allow-subdomains: false
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
