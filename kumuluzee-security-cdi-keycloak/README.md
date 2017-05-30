# KumuluzEE Security CDI sample with Keycloak

> Build a CDI enabled REST service which utilizes KumuluzEE Security to secure the resources with Keycloak using 
standard Java annotations and pack it as a KumuluzEE microservice

The objective of this sample is to show how to develop a microservice that uses KumuluzEE Security extension to
secure the CDI enabled REST resources. In this sample we develop a simple REST service, promote it to a CDI bean, 
secure it using Keycloak and pack it as a KumuluzEE microservice. This tutorial will guide you through all the 
necessary steps. You will first add KumuluzEE dependencies into pom.xml. To develop the REST service, you will use the 
standard JAX-RS 2 API. Required knowledge: basic familiarity with JAX-RS 2, CDI, OAuth2, Keycloak and basic concepts of 
REST.

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

To run this sample you will need a Keycloak instance. Here is an example on how to quickly run a Keycloak instance with Docker:

```bash
$ docker run \
         -e KEYCLOAK_USER=<USERNAME> \
         -e KEYCLOAK_PASSWORD=<PASSWORD> \
         jboss/keycloak
```

## Usage

The example uses Maven to build and run the microservice.

1. Build the sample using Maven:

    ```bash
    $ cd kumuluzee-security-cdi-keycloak
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
* JAX-RS REST resource - http://localhost:8080/v1/customers

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to secure a simple REST microservice using Keycloak and pack it 
as a KumuluzEE microservice. We will extend the existing [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs),
 with CDI and KumuluzEE Security extension implemented by Keycloak. Therefore, first complete the existing JAX-RS sample 
 tutorial, or clone the JAX-RS sample code. 

We will follow these steps:
* Complete the tutorial for [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) 
or clone the existing sample
* Add Maven dependencies
* Configure Keycloak
* Enable CDI
* Implement security
* Build the microservice
* Run it
* Test security

### Add Maven dependencies

Since your existing starting point is the existing KumuluzEE JAX-RS REST sample, you should already have the 
dependencies for `kumuluzee-bom`, `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-jax-rs-jersey` configured 
in `pom.xml`.

Add the `kumuluzee-cdi-weld` and `kumuluzee-security-keycloak` dependencies:
```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-cdi-weld</artifactId>
</dependency>
<dependency>
    <groupId>com.kumuluz.ee.security</groupId>
    <artifactId>kumuluzee-security-keycloak</artifactId>
    <version>${kumuluzee-security.version}</version>
</dependency>
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

### Configure Keycloak

Log into the Keycloak using your admin account and create a new realm, e.g. **customers-realm**. Then create two new 
clients. One will be used to retrieve access tokens and the other one wil be used only to verify issued tokens. 
The first client, e.g. **customers-app**, should be of **public** access type. Under **Valid redirect URIs** enter 
*http://localhost*. Create the second client and set its access type to **bearer-only** and give it a name, e.g. 
**customers-api**. Create a user and set the credentials. Then create a couple of roles and assign them to the new 
user. When finished, open up the new client and copy the configuration under *Installation/Keycloak OIDC JSON* and save 
it for later.

### Enable CDI

Enable CDI by creating the **beans.xml** file in src/main/resources/META-INF directory.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd"
       bean-discovery-mode="annotated">
</beans>
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

Then take the Keycloak JSON configuration and set it as an environment variable by using the following key:

`KUMULUZEE_SECURITY_KEYCLOAK_JSON`

In this sample, we use environment variables to store the configuration; however the configuration can be also stored 
in a file or a config server. Please refer to KumuluzEE Config for more information. 

Promote the JAX-RS resource to a CDI bean, enable CDI security and add security constraints:

```java
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
@Secure
public class CustomerResource {

    @GET
    @PermitAll
    public Response getAllCustomers() {
        ...
    }

    @GET
    @Path("{customerId}")
    public Response getCustomer(@PathParam("customerId") String customerId) {
        ...
    }

    @POST
    @RolesAllowed("user")
    public Response addNewCustomer(Customer customer) {
        ...
    }

    @DELETE
    @Path("{customerId}")
    @RolesAllowed("user")
    public Response deleteCustomer(@PathParam("customerId") String customerId) {
        ...
    }
}
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.

### Test security

Try by creating a new customer first:

```bash
$ curl -X POST \
  http://localhost:8080/v1/customers \
  -H 'content-type: application/json' \
  -d '{
	"id": 1,
	"firstName": "John",
	"lastName": "Doe"
  }'
```

If everything was implemented correctly you should receive a 401 HTTP response error.

Now obtain an access token from Keycloak (**NOTE**: To do this, you will first have to retrieve the client ID your 
public client on Keycloak and the username and password of your Keycloak user):

```bash
$ curl -X POST \
  http://localhost:8082/auth/realms/customers/protocol/openid-connect/token \
  -H 'content-type: application/x-www-form-urlencoded' \
  -d 'grant_type=password&client_id=customers-app&username=johndoe&password=abc123'
```

After receiving the access token try again by creating a new customer, now with the access token in the request:
  
```bash
$ curl -X POST \
  http://localhost:8080/v1/customers \
  -H 'authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJUeWlBbFVPMTlKZm82T0N6NWE1T0pVWF94MDV0dndsT3BKVnRqUk4ycExBIn0.eyJqdGkiOiJhMjIxZDc5Yi01ZmY4LTRlMDEtYjVmOS1jODRkMGM1MDRmNjYiLCJleHAiOjE0OTU1MjQ0ODEsIm5iZiI6MCwiaWF0IjoxNDk1NTIzODgxLCJpc3MiOiJodHRwczovL3Rlc3Qtb3JjbC52bS5hcGltYW5hZ2VyLmNsb3VkLnNpL2F1dGgvcmVhbG1zL2t1bXVsdXotYXBpIiwiYXVkIjoia3VtdWx1ei1hcGktZnJvbnRlbmQiLCJzdWIiOiJhNDdkY2Y0Yy1hNTVlLTRjNTktODRkNy02NTkwMjM5NjlmZmUiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJrdW11bHV6LWFwaS1mcm9udGVuZCIsIm5vbmNlIjoiZThiYzIxYmUtY2JiMC00NGYzLTg5NDktNGE5YmY2NDdiYTJiIiwiYXV0aF90aW1lIjoxNDk1NTIzODgwLCJzZXNzaW9uX3N0YXRlIjoiODRkYzkyYTAtZmI2Mi00YTkzLTg0NGItOTkwMzU4NjVjN2FlIiwiYWNyIjoiMSIsImNsaWVudF9zZXNzaW9uIjoiYWY0NzU1MWQtZGVjOS00YjA0LWIxZGItNTc3MTQ0Zjc4YjY5IiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6MzAwMiIsImh0dHA6Ly9hcGkua3VtdWx1ei5jb20iLCJodHRwczovL3Rlc3Qtb3JjbC52bS5hcGltYW5hZ2VyLmNsb3VkLnNpIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJhcGlwdWJsaXNoZXIiLCJhZG1pbiIsImFwaWFkbWluIiwiYXBpdXNlciIsInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJyZWFsbS1tYW5hZ2VtZW50Ijp7InJvbGVzIjpbIm1hbmFnZS11c2VycyIsInZpZXctdXNlcnMiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJ2aWV3LXByb2ZpbGUiXX19LCJuYW1lIjoiQmVuamFtaW5uIEthc3RlbGljYyIsInByZWZlcnJlZF91c2VybmFtZSI6ImJlbmphbWluazIiLCJnaXZlbl9uYW1lIjoiQmVuamFtaW5uIiwiZmFtaWx5X25hbWUiOiJLYXN0ZWxpY2MiLCJlbWFpbCI6ImJlbmphbWluLmthc3RlbGljQGNsb3VkLnNpIn0.DteMq7VPuwrghiHE_f0DSlcznLkkoe2fVpXj6jgXCm2ei-f1TK2AtJ3bni-FndQcVYu6VE_-KDCVx0L4wMLsOLTVmrUKQDBzL04P51-h9bj5Oi7Ri0gAkQLmr0Ftg_Ixr5NGkvGAgmvmngZ0JIRWzue7QZdUU3XwJc8mZJYlRe9kqaSCg-ALvQiwe27I6u5jSyb0U8XM8fIzDj5ubwLqfeU_FopLrQoZ5ZjdFCgN_lR4KtlbkvKim1omyuOaPkJkddX3269KKT6veLkmRNxlOrznXtzVgeDwziSVFNoSTDEhqM7KjsfxE0tU2KqV7MLCzknWnjHW1A93_1_Em1MmRw' \
  -H 'content-type: application/json' \
  -d '{
	"id": 1,
	"firstName": "John",
	"lastName": "Doe"
  }'
```

A customer should now be created successfully and you should receive a 204 HTTP status code.
