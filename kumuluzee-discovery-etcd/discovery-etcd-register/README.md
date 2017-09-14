# KumuluzEE Discovery &mdash; register service with etcd 

> Develop a REST KumuluzEE microservice and register it with etcd.

The objective of this sample is to show how to register a REST service with etcd using KumuluzEE Discovery extension.
This tutorial will guide you through all the necessary steps. You will add KumuluzEE dependencies into pom.xml.
You will use existing JAX-RS sample, described [here](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs).
Required knowledge: basic familiarity with JAX-RS and basic concepts of REST and JSON; basic familiarity with etcd.

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

## Usage

The example uses maven to build and run the microservice.

1. Build the sample using maven:

    ```bash
    $ cd discovery-samples/discovery-register
    $ mvn clean package
    ```

2. Start local etcd instance in another terminal:

    ```bash
    $ etcd
    ```

3. Run the sample:
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
* JAX-RS REST resource - http://localhost:8081/v1/customers

The application is registered with etcd. You can discover it using one of the discover samples:
* [discover-etcd-servlet sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery-etcd/discovery-etcd-discover-servlet)
* [discover-etcd-jaxrs sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery-etcd/discovery-etcd-discover-jaxrs)

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to register KumuluzEE microservice with etcd. 
We will use existing [sample Customer REST service](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) with the following resources:
* GET http://localhost:8081/v1/customers - list of all customers 
* GET http://localhost:8081/v1/customers/{customerId} – details of customer with ID {customerId}
* POST http://localhost:8081/v1/customers – add a customer
* DELETE http://localhost:8081/v1/customers/{customerId} – delete customer with ID {customerId}

We will follow these steps:
* Import a Maven sample, mentioned above, in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependency to KumuluzEE Discovery extension
* Annotate JAX-RS Application class with @RegisterService annotation
* Build the microservice
* Run it

### Add Maven dependencies

Add the `kumuluzee-discovery-etcd` dependency to the sample:
```xml
<dependencies>
    ...
    
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

Add the `kumuluzee-maven-plugin` build plugin to package microservice as exploded:

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

### Annotate JAX-RS Application

Add the `@RegisterService` annotation to JAX-RS Application class (CustomerApplication.java):

```java
@RegisterService
@ApplicationPath("v1")
public class CustomerApplication extends Application {
}
```

### Add required configuration for the service discovery

You can add configuration using any KumuluzEE configuration source.

For example, you can use config.yml file, placed in resources folder:
```yaml
kumuluzee:
  name: customer-service
  env:
    name: dev
  version: 1.0.0
  server:
    base-url: http://localhost:8081
    http:
      port: 8081
  discovery:
    etcd:
      hosts: http://192.168.99.100:2379
    ttl: 20
    ping-interval: 15
```

Port 8081 is used because we want to run another microservice on default port, which discovers this service on port 8080.

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.