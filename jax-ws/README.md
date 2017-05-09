# KumuluzEE JAX-RS sample

> JAX-WS usage sample with KumuluzEE

This sample demonstrates how to configure and use Java EE JAX-WS technologies using KumuluzEE.

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
    $ cd jax-ws
    $ mvn clean package
    ```

2. Run the sample:

    ```bash
    $ java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```

    in Windows environment use the command
    ```
    java -cp target/classes;target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
The application/service can be accessed on the following URL:
* JAX-WS SOAP endpoint - http://localhost:8080/CustomerEndpoint
* JAX-WS SOAP WSDL - http://localhost:8080/CustomerEndpoint?WSDL

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a simple SOAP service using standard JAX-WS 2.2 API and pack it as a KumuluzEE microservice. 
We will develop a simple Customer SOAP service with the following operation:
* GetCustomers - list of all customers 

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet and JAX-WS)
* Implement the service using standard JAX-WS 2.2 API
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

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-jax-ws-metro` dependencies:
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
            <artifactId>kumuluzee-jax-ws-metro</artifactId>
        </dependency>
    </dependencies>
```

Add the `maven-dependency-plugin` build plugin to copy all the necessary dependencies into target folder:

```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>2.10</version>
                <executions>
                    <execution>
                        <id>copy-dependencies</id>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <includeScope>runtime</includeScope>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

### Implement the service

Typical approach to the implementation of a SOAP web service using the Top-Down approach by generating implementation from a WSDL file. To demonstrate KumuluzEE microservice implementation with JAX-WS 2.2 API usage, we will use the Bottom-Up approach in this tutorial. In case you are generating your implementation from a WSDL file, you can skip to point where service is configured in file `sun-jaxws.xml`.

For example, imagine that you are creating a SOAP web service, which returns all customers. First, let us create a sample Java class annotated with JAXB annotations which will hold a single customer, for example:

```java
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Customer",
        propOrder = {
            "id",
            "firstName",
            "lastName"
        }
)
public class Customer implements Serializable {

    @XmlElement(required = true)
    private String id;
    @XmlElement(required = true)
    private String firstName;
    @XmlElement(required = true)
    private String lastName;

    //get and set methods
}
```

In similar fashion create a Java class that will hold a list of customers:

```java
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "CustomersList",
        propOrder = {
            "customer"
        }
)
public class CustomersList implements Serializable {

    @XmlElement(required = true)
    private List<Customer> customer;

    //get and set methods
}
```

Create Response Java object and corresponding message wrapper:

```java
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {
            "output"
        }
)
@XmlRootElement(name = "GetCustomersResponse")
public class GetCustomersResponse implements Serializable {

    @XmlElement(required = true)
    private GetCustomersResponseMessage output;

     //get and set methods
```

```java
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "GetCustomersResponseMessage",
        propOrder = {
            "customers"
        }
)
public class GetCustomersResponseMessage implements Serializable {

    @XmlElement(required = true)
    private CustomersList customers;

 //get and set methods
}
```

Finally, implement the service using JAX-WS annotations:

```java
@WebService(
        targetNamespace = "http://kumuluz.com/samples/jax-ws/customers/v1",
        serviceName = "CustomerEndpoint",
        portName = "CustomerEndpointSOAP"
)
public class CustomerEndpoint {

    @WebMethod(operationName = "GetCustomers", action = "http://kumuluz.com/samples/jax-ws/customers/v1/GetCustomers")
    @WebResult(name = "output")
    @ResponseWrapper(
            localName = "GetCustomersResponse",
            targetNamespace = "http://kumuluz.com/samples/jax-ws/customers/v1",
            className = "com.kumuluz.ee.samples.jaxws.GetCustomersResponse"
    )
    public GetCustomersResponseMessage getCustomers() {

        List<Customer> customers = new ArrayList<>();
        //TODO: Add customers to list

        CustomersList customersList = new CustomersList();
        customersList.setCustomer(customers);

        GetCustomersResponseMessage message = new GetCustomersResponseMessage();
        message.setCustomers(customersList);

        return message;
    }
}
```

Customer is an Java POJO with get and set methods, for example:
```java
public class Customer {

    private String id;

    private String firstName;

    private String lastName;

    //get and set methods
}
```

Create directory `webapp/WEB-INF` in `resources` folder and in this directory file `sun-jaxws.xml`. The file should contain the configuration as shown below. Replace the name of your implementation class (com.kumuluz.ee.samples.jaxws.CustomerEndpoint). To modify the URL of your web service, you can change the `url-pattern` property.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<endpoints xmlns="http://java.sun.com/xml/ns/jax-ws/ri/runtime"
           version="2.0">
    <endpoint name="WebServiceImpl"
              implementation="com.kumuluz.ee.samples.jaxws.CustomerEndpoint"
              url-pattern="/CustomerEndpoint"/>
</endpoints>
```

### Run the service

To run the example, use the commands as described in previous sections.