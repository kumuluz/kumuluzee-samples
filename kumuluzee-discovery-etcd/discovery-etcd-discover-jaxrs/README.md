# KumuluzEE Discovery &mdash; discover services in JAX-RS service 

> Develop a REST service that discovers a microservice registered with etcd.

The objective of this sample is to show how to discover a service, registered with etcd using KumuluzEE Discovery 
extension. The tutorial will guide you through all the necessary steps. You will add KumuluzEE dependencies into pom
.xml. You will develop a simple REST service, which uses KumuluzEE Discovery extension for service discovery.
Required knowledge: basic familiarity with JAX-RS and REST; basic familiarity with etcd.

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

To run this sample you will need an etcd instance. Note that such setup with only one etcd node is not viable for 
production environments, but only for developing purposes. Here is an example on how to quickly run an etcd instance 
with docker:

   ```bash
    $ docker run -d --net=host \
        --name etcd \
        --volume=/tmp/etcd-data:/etcd-data \
        quay.io/coreos/etcd:v3.1.7 \
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
        --auto-compaction-retention 1
   ```


You will also need a registered service instance. You can use the
 [discovery-etcd-register](http://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery-etcd/discovery-etcd-register) sample.

## Usage

The example uses maven to build and run the microservice.

1. Build the sample using maven:

    ```bash
    $ cd discovery-samples/discovery-discover-jaxrs
    $ mvn clean package
    ```

2. Start local etcd instance and another microservice, which registers to etcd:

    You can find instructions in discovery-register sample, mentioned above.

3. Run the sample:
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
* JAX-RS REST resource, discovery using annotations - http://localhost:8080/v1/discover
* JAX-RS REST resource, programmatic discovery - http://localhost:8080/v1/programmatic

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a service, which uses KumuluzEE Discovery extension.
We will develop a simple REST service with the following resources:
* GET http://localhost:8080/v1/discover/url - discovered service's url
* GET http://localhost:8080/v1/discover - list of all customers from discovered service + discovered service's url
* POST http://localhost:8080/v1/discover - add a customer to discovered service
* GET http://localhost:8080/v1/programmatic/{serviceName}/{serviceVersion}/{environment} - discovered service's url

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet, JAX-RS)
* Add Maven dependency to KumuluzEE Discovery extension
* Implement the service
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

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty`, `kumuluz-jax-rs-jersey` and `kumuluzee-discovery` dependencies:
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
        <groupId>com.kumuluz.ee.discovery</groupId>
        <artifactId>kumuluzee-discovery-etcd</artifactId>
        <version>${kumuluzee-discovery.version}</version>
    </dependency>
</dependencies>
```

Add the `kumuluzee-maven-plugin` build plugin to package microservice as uber-jar:

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
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugin>
    </plugins>
</build>
```

or exploded:

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

### Implement the servlet


Register your module as JAX-RS service and define the application path. You could do that in web.xml or
for example with `@ApplicationPath` annotation:

```java
@ApplicationPath("v1")
public class DiscoverApplication extends Application {
}
```

Implement JAX-RS resource, which will use annotations for service discovery:

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("discover")
@RequestScoped
public class DiscoverResource {

    @Inject
    @DiscoverService(value = "customer-service", version = "1.0.x", environment = "dev")
    private WebTarget target;

    @GET
    @Path("url")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getUrl() {
        return Response.ok(target.getUri().toString()).build();
    }

    @GET
    public Response getProxiedCustomers() {
        WebTarget service = target.path("v1/customers");

        Response response;
        try {
            response = service.request().get();
        } catch (ProcessingException e) {
            return Response.status(408).build();
        }

        ProxiedResponse proxiedResponse = new ProxiedResponse();
        proxiedResponse.setResponse(response.readEntity(String.class));
        proxiedResponse.setProxiedFrom(target.getUri().toString());

        return Response.ok(proxiedResponse).build();
    }

    @POST
    public Response addNewCustomer(Customer customer) {
        WebTarget service = target.path("v1/customers");

        Response response;
        try {
            response = service.request().post(Entity.json(customer));
        } catch (ProcessingException e) {
            return Response.status(408).build();
        }

        return Response.fromResponse(response).build();
    }
}
```

In the example above, we inject a `WebTarget` resource using `@DiscoverService` annotation. KumuluzEE Discovery 
extension uses NPM-like versioning, so by specifying version "1.0.x", we always get the latest patch of 1.0.x version
microservice, registered with etcd.

We use two POJO's in this example: Customer, which is the same as in the discovery-register sample
and ProxiedResponse, which we use for returning discovered service's response and add it's port. They can be implemented
as follows:

```java
public class Customer {

    private String id;
    private String firstName;
    private String lastName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
```

```java
public class ProxiedResponse {

    private String response;
    private String proxiedFrom;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getProxiedFrom() {
        return proxiedFrom;
    }

    public void setProxiedFrom(String proxiedFrom) {
        this.proxiedFrom = proxiedFrom;
    }
}
```

Implement JAX-RS resource, which will use programmatic service discovery:

```java
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("programmatic")
@RequestScoped
public class ProgrammaticDiscoveryResource {

    @Inject
    DiscoveryUtil discoveryUtil;

    @GET
    @Path("{serviceName}/{serviceVersion}/{environment}")
    public Response getInstances(@PathParam("serviceName") String serviceName,
                                 @PathParam("serviceVersion") String serviceVersion,
                                 @PathParam("environment") String environment) {
        Optional<List<URL>> instances = discoveryUtil.getServiceInstances(serviceName, serviceVersion, environment);

        if(instances.isPresent()) {
            return Response.ok(instances.get()).build();
        } else {
            return Response.noContent().build();
        }
    }
}
```

In the example above, we use JAX-RS path parameters to discover the desired registered instance.
We inject a `DiscoveryUtil` resource and then call its function `getInstances(serviceName, serviceVersion, environment)`.

### Add required configuration for the service discovery

You can add configuration using any KumuluzEE configuration source.

For example, you can use config.yml file, placed in resources folder:
```yaml
kumuluzee:
  discovery:
    etcd:
      hosts: http://127.0.0.1:2379
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
