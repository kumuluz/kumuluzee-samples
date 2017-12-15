# KumuluzEE Reactive &mdash; listen to Vert.x event bus

> Develop a REST microservice that listens to a given address on Vert.x event bus

The objective of this sample is to show how to listen to Vert.x event bus using KumuluzEE Reactive extension. The tutorial will guide you through all the necessary steps. You will add KumuluzEE dependencies into `pom.xml`. You will develop a simple annotated method, which uses KumuluzEE Reactive extension to listen for messages on Vert.x event bus. Required knowledge: basic familiarity with Vert.x.

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
     
3. Message will be printed out in the terminal when the microservice receives it.

4. Latest five messages can be accessed on the following URL:
    * JAX-RS REST resource - http://localhost:8080/v1/vertx/messages with a GET request

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a Vert.x event bus listener with the help of the KumuluzEE Reactive Vert.x extension.
We will develop a simple annotated method which will be invoked when the message is received.

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet, JAX-RS, CDI)
* Add Maven dependency to KumuluzEE Reactive Vertx extension
* Implement the service and the onMessage method
* Add configuration for Vert.x
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

### Implement the service and the onMessage method

Register your module as JAX-RS service and define the application path. You could do that in web.xml or for example with `@ApplicationPath` annotation:

```java
@ApplicationPath("v1")
public class VertxApplication extends Application {
}
```

Implement a class e.g. VertxEventListener annotated with `@ApplicationScoped` that has a method. The method is annotated with `@ReactiveEventListener(address = "tacos")` and takes a parameter of type `Message<Object>` that contains data of the received message.

We will store the received messages in a `List`. Method `getFiveLastMessages()` returnes the latest five messages received.

In the example the address name is defiined within the annotation. You can also change the method name to match your preferences.

```java
@ApplicationScoped
public class VertxEventListener {

	private static final Logger log = Logger.getLogger(VertxEventListener.class.getName());

	private List<String> messages = new ArrayList<>();
	
	@ReactiveEventListener(address = "tacos")
	public void onMessage(Message<Object> event) {
		if(event.body() != null) {
			messages.add((String) event.body());
			log.info("New message received: " + event.body());
		} else {
			log.warning("Error when receiving messages.");
		}
	}
	
	public List<String> getFiveLastMessages() {
		if(messages.size() <= 5) {
			return messages;
		}
		return messages.subList(messages.size() - 5, messages.size());
	}

}
```

When no address is supplied to the annotation, the default address `listener` is used.

Implement JAX-RS resource, with a GET method for displaying the last 5 received  messages. Inject the `VertxEventListener` and retrieve the messages:

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("vertx")
@RequestScoped
public class VertxResource {

	@Inject
	VertxEventListener vertxEventListener;
	
	@GET
	@Path("messages")	
	public Response getMessages() {
		return Response.ok(vertxEventListener.getFiveLastMessages()).build();
	}
	
}
```


### Add configuration for Vert.x

You can configure Vert.x using any KumuluzEE configuration source.

For example, you can use `config.yml` file, placed in resources folder.
In this example we configured `config.yml` as shown below:

```yaml
kumuluzee:
  name: vertx-listener
  version: 1.0.0
  server:
    http:
      port: 8081
  reactive:
    vertx:
      clustered: true
      cluster-host: localhost
      cluster-port: 0 
```

Setting the `clustered` tag to `true` allows multiple Vert.x instances to form a single, distributed, event bus. `cluster-host` and `cluster-port` determine which address will be used for cluster communication with other Vert.x instances. Their default values are `localhost` and `0`, which means a random free port will be chosen.

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.

You can either run [`kumuluzee-reactive-vertx-publisher`](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-reactive-vertx/kumuluzee-reactive-vertx-publisher) or [`verticle-publisher`](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-reactive-vertx/simple-verticles/verticle-publisher) to test the functionalities of this microservice.