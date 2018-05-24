# KumuluzEE JAX-WS CXF SOAP web service sample

> Develop a SOAP web service using JAX-WS CXF API and pack it as a KumuluzEE microservice.

The objective of this sample is to show how to develop a SOAP web service using JAX-WS CXF implementation and pack it as a 
KumuluzEE microservice. The tutorial will guide you through the necessary steps to develop webservice using top-down approach. You will add KumuluzEE 
dependencies into pom.xml. To 
develop the SOAP web service, you will use CXF implementation of JAX-WS. Required knowledge: basic familiarity with JAX-WS and basic concepts of SOAP, WSDL and 
XML. Sample shows how to use dependency injection in webservices.

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
    $ cd jax-ws/jax-ws-cxf
    $ mvn clean package
    ```

2. Run the sample:
* Uber-jar:

    ```bash
    $ java -jar target/${project.build.finalName}.jar
    ```
    
    in Windows environment use the command
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
* JAX-WS SOAP endpoint - http://localhost:8080/soap/customers/1.0
* JAX-WS SOAP WSDL - http://localhost:8080/soap/customers/1.0?wsdl

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a simple SOAP service using JAX-WS CXF API and pack it as a KumuluzEE microservice. 
We will develop a simple Customer SOAP service with the following operation:
* GetCustomers - list of all customers 

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet and JAX-WS cxf)
* Implement the service using JAX-WS CXF API
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

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-jax-ws-cxf` dependencies. We will add `kumuluzee-cdi-weld` just to demonstrate 
dependency injection. It is not required to run JAX-WS CXF webservice.
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
            <artifactId>kumuluzee-jax-ws-cxf</artifactId>
        </dependency>
        <!-- Remove if you don't need cdi -->
        <dependency>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-cdi-weld</artifactId>
        </dependency>
    </dependencies>
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

### Describe the service

Typical approach to the implementation of a SOAP web service is using the Top-Down approach by generating implementation from a WSDL file.

For example, imagine that you are creating a SOAP web service, which returns all customers. First, let us create a sample wsdl file describing our 
contract, for example:

```xml
<?xml version='1.0' encoding='UTF-8'?>
<wsdl:definitions xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
                  xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
                  xmlns:xs="http://www.w3.org/2001/XMLSchema"
                  xmlns:cust="http://kumuluz.com/samples/jax-ws/cxf/customers/1.0"
                  name="CustomerEndpoint"
                  targetNamespace="http://kumuluz.com/samples/jax-ws/cxf/customers/1.0">
    <wsdl:types>
        <xs:schema xmlns:tns="http://kumuluz.com/samples/jax-ws/cxf/customers/1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="1.0"
                   targetNamespace="http://kumuluz.com/samples/jax-ws/cxf/customers/1.0">
            <xs:element name="GetCustomers" type="tns:GetCustomers"/>
            <xs:element name="GetCustomersResponse" type="tns:GetCustomersResponse"/>
            <xs:complexType name="GetCustomers">
                <xs:sequence/>
            </xs:complexType>
            <xs:complexType name="GetCustomersResponse">
                <xs:sequence>
                    <xs:element name="customers" type="tns:Customer" minOccurs="0" maxOccurs="unbounded"/>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="Customer">
                <xs:sequence>
                    <xs:element name="id" type="xs:string"/>
                    <xs:element name="firstName" type="cust:string32"/>
                    <xs:element name="lastName" type="cust:string32"/>
                </xs:sequence>
            </xs:complexType>
            <xs:simpleType name="string32">
                <xs:restriction base="xs:string">
                    <xs:minLength value="1"/>
                    <xs:maxLength value="32"/>
                </xs:restriction>
            </xs:simpleType>
        </xs:schema>
    </wsdl:types>
    <wsdl:message name="GetCustomers">
        <wsdl:part name="parameters" element="cust:GetCustomers"/>
    </wsdl:message>
    <wsdl:message name="GetCustomersResponse">
        <wsdl:part name="parameters" element="cust:GetCustomersResponse"/>
    </wsdl:message>
    <wsdl:portType name="CustomerEndpoint">
        <wsdl:operation name="getCustomers">
            <wsdl:input message="cust:GetCustomers"/>
            <wsdl:output message="cust:GetCustomersResponse"/>
        </wsdl:operation>
    </wsdl:portType>
    <wsdl:binding name="CustomerEndpointSOAP" type="cust:CustomerEndpoint">
        <soap:binding transport="http://schemas.xmlsoap.org/soap/http" style="document"/>
        <wsdl:operation name="getCustomers">
            <soap:operation soapAction="http://kumuluz.com/samples/jax-ws/cxf/customers/1.0/GetCustomers"/>
            <wsdl:input>
                <soap:body use="literal"/>
            </wsdl:input>
            <wsdl:output>
                <soap:body use="literal"/>
            </wsdl:output>
        </wsdl:operation>
    </wsdl:binding>
    <wsdl:service name="CustomerEndpoint">
        <wsdl:port name="CustomerEndpointSOAP" binding="cust:CustomerEndpointSOAP">
            <soap:address location="https://gpor89.github.com/soap/CustomerEndpoint"/>
        </wsdl:port>
    </wsdl:service>
</wsdl:definitions>
```

Wsdl describes service with name CustomerEndpoint having one operation called getCustomers which returns list of Customer business objects.
Each name and surname data must be set and at most 32 characters long. We will demonstrate schema validation and interceptors.

### Generate interface

To implement our service as fast as possible we should configure `jaxws-maven-plugin` build plugin to generate java classes and interfaces for us. Another option is to 
use wsimport tool when we don't want to use maven.

```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>jaxws-maven-plugin</artifactId>
                <version>2.5</version>
                <executions>
                    <execution>
                        <id>wsimport-from-jdk</id>
                        <goals>
                            <goal>wsimport</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <wsdlDirectory>src/main/resources/wsdls</wsdlDirectory>
                    <wsdlFiles>
                        <wsdlFile>customers.wsdl</wsdlFile>
                    </wsdlFiles>
                    <keep>true</keep>
                    <sourceDestDir>target/generated-sources/apt</sourceDestDir>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
Maven package command will bring us generated CustomerEndpoint java interface from wsdl:

```bash
$ mvn package
```

Create new java class which implements CustomerEndpoint representing implementation of our service:

```java
@WsContext(contextRoot = "/soap", urlPattern = "/customers/1.0")
@ApplicationScoped
@WebService(serviceName = "CustomerEndpoint", portName = "CustomerEndpointSOAP", targetNamespace = "http://kumuluz.com/samples/jax-ws/cxf/customers/1.0",
        endpointInterface = "com.kumuluz.samples.jax_ws.cxf.customers._1.CustomerEndpoint", wsdlLocation = "/wsdls/customers.wsdl")
@SchemaValidation
@Interceptors(WsInterceptor.class)
@HandlerChain(file = "/META-INF/handler-chains.xml")
public class CustomerEndpointBean implements CustomerEndpoint {

    private static final Logger LOG = Logger.getLogger(CustomerEndpointBean.class.getName());

    @Inject
    private CustomersService customersService;

    @Resource(name = "wsContext")
    private WebServiceContext webServiceContext;

    @Override
    public GetCustomersResponse getCustomers(final GetCustomers parameters) {

        String soapAction = (String) webServiceContext.getMessageContext().get("SOAPAction");
        LOG.info("Soap action: " + soapAction);

        GetCustomersResponse response = new GetCustomersResponse();
        response.getCustomer().addAll(customersService.getCustomers());

        return response;
    }
}
```

With WsContext annotation we can define endpoint contextRoot and url where service listens for incoming messages.
wsdlLocation attribute must be present if we need schema validation in order with @SchemaValidation annotation.

Interceptor logic may be configured with Interceptors or HandlerChain annotations. 

Replace the WebService parameters according to your wsdl service definition.

@ApplicationScoped, @Interceptors, @HandlerChain and @Inject annotations are present for demonstration purpose therefore are not required
 to run JAX-WS webservice. 

### Run the service

To run the example, use the commands as described in previous sections.
