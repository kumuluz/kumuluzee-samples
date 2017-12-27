# KumuluzEE Reactive &mdash; Vert.x discovery bridge

> Develop a REST microservice that acts as a bridge between KumuluzEE Discovery and Vert.x Service Discovery

The objective of this sample is to show how to create a discovery bridge using KumuluzEE Reactive extension.
The tutorial will guide you through all the necessary steps. You will add KumuluzEE dependencies into `pom.xml`.
Required knowledge: basic familiarity with Vert.x.

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

To run this sample you will need either an etcd or a Consul instance. We will use an etcd instance in this example.

### etcd

Note that such setup with only one etcd node is not viable for 
production environments, but only for developing purposes. Here is an example on how to quickly run an etcd instance 
with docker:

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
   
### Consul

Note that such setup with only one node is not viable for 
production environments, but only for developing purposes. Download Consul and run it in development mode with the 
following command:

   ```bash
    $ consul agent -dev
   ```

## Usage

The example uses maven to build and run the microservice.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-reactive-vertx/kumuluzee-reactive-vertx-listener
    $ mvn clean package
    ```

2. Run the sample:

    * Exploded:

    ```bash
    $ java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
    in Windows environment use the command
    ```batch
    java -cp target/classes;target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
The application/service can be accessed on the following URL:
* JAX-RS REST resource - http://localhost:8080/v1/vertx/status

You can register a service to Consul using:
* [discovery-consul-register sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery-consul/discovery-consul-register)

You can register a service to etcd using:
* [discovery-etcd-register sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery-etcd/discovery-etcd-register)

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to setup a bridge between KumuluzEE Discovery and Vert.x Service Discovery.
* GET http://localhost:8080/v1/vertx/status - status of the service

We will follow these steps:
* Import a Maven sample, mentioned above, in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependency to KumuluzEE Reactive extension
* Add Maven dependency to KumuluzEE Discovery (etcd or Consul)
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

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty`, `kumuluzee-jax-rs-jersey` and `kumuluzee-cdi-weld` dependencies:
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
         <groupId>com.kumuluz.ee</groupId>
         <artifactId>kumuluzee-cdi-weld</artifactId>
    </dependency>
</dependencies>
```
Alternatively, we could add the `kumuluzee-microProfile-1.0`, which adds the MicroProfile 1.0 dependencies (JAX-RS, CDI, JSON-P, and Servlet).

Add the `kumuluzee-reactive-vertx` dependency:
```xml
<dependency>
	<groupId>com.kumuluz.ee.reactive</groupId>
	<artifactId>kumuluzee-reactive-vertx</artifactId>
	<version>${kumuluzee-reactive-vertx.version}</version>
</dependency>
```

Add the either `kumuluzee-discovery-etcd` or `kumuluzee-discovery-consul`.
We will use etcd in this example.
```xml
<dependency>
    <groupId>com.kumuluz.ee.discovery</groupId>
    <artifactId>kumuluzee-discovery-etcd</artifactId>
    <version>${kumuluzee-discovery.version}</version>
</dependency>
```
or
```xml
<dependency>
    <groupId>com.kumuluz.ee.discovery</groupId>
    <artifactId>kumuluzee-discovery-consul</artifactId>
    <version>${kumuluzee-discovery.version}</version>
</dependency>
```

Add the `kumuluzee-maven-plugin` build plugin to package microservice:

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

Register your module as JAX-RS service and define the application path. You could do that in web.xml or for example with `@ApplicationPath` annotation:

```java
@ApplicationPath("v1")
public class VertxApplication extends Application {
}
```

Implement JAX-RS resource, with a GET method to obtain service status.

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("vertx")
@RequestScoped
public class VertxResource {
	
	@GET
	@Path("/status")
	public Response getStatus() {
		return Response.ok().build();
	}
	
}
```

### Add configuration for Vert.x

You can configure Vert.x using any KumuluzEE configuration source.

For example, you can use `config.yml` file, placed in resources folder.
In this example we configured `config.yml` to use etcd as shown below:

```yaml
kumuluzee:
  name: vertx-bridge
  version: 1.0.0
  env:
    name: dev
  server:
    base-url: http://localhost:8080
    http:
      port: 8080
  discovery:
    etcd:
      hosts: http://localhost:2379
    ttl: 20
    ping-interval: 15
  reactive:
    vertx:
      clustered: true
      cluster-host: localhost
      cluster-port: 0
      discovery:
        env:
          name: dev
        ttl: 20
        ping-interval: 15    
```

Setting the `clustered` tag to `true` allows multiple Vert.x instances to form a single, distributed, event bus, thus
enabling the service to discover other Vert.x services. When no tags are provided within `reactive.vertx.discovery`
they are taken from `kumuluzee.discovery`.

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.

For registering a Vert.x service and getting a reference to a service registered with KumuluzEE Discovery you can use
[`verticle-discovery`](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-reactive-vertx/simple-verticles/verticle-discovery).