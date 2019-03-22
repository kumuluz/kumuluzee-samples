# KumuluzEE Logs sample with Fluentd implementation

> Build a REST service which utilizes a built-in logging framework with Fluentd implementation to log basic metrics and pack it as a KumuluzEE microservice

The objective of this sample is to demonstrate how to use the built-in logging framework with Fluentd implementation to log basic metrics.

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

- Docker

## Usage

The example uses maven to build and run the microservices.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-logs-fluentd
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
* JAX-RS REST resource - http://localhost:8080/v1/customers

To shut down the example simply stop the processes in the foreground.

## Tutorial
This tutorial will guide you through the steps required to use KumuluzEE Logs and pack the application as a KumuluzEE microservice. We will extend the existing [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs).
Therefore, first complete the existing JAX-RS sample tutorial, or clone the JAX-RS sample code.

We will follow these steps:
* Complete the tutorial for [KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) or clone the existing sample
* Add Maven dependencies
* Add KumuluzEE Logs support
* Add fluentd logging configuration
* Build the microservice
* Run it

### Add Maven dependencies

Since your existing starting point is the existing KumuluzEE JAX-RS REST sample, you should already have the dependencies for `kumuluzee-bom`, `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-jax-rs-jersey` configured in `pom.xml`.

Add the `kumuluzee-cdi-weld` and `kumuluzee-logs-fluentd` dependencies:
```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-cdi-weld</artifactId>
</dependency>
<dependency>
    <groupId>com.kumuluz.ee.logs</groupId>
    <artifactId>kumuluzee-logs-fluentd</artifactId>
    <version>${kumuluz-logs.version}</version>
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
### Create Fluentd configuration file
In this sample we will use the following configuration which is available in the sample. The configuration exposes endpoint on the port 24224 for the purpose of collecting logs from Java application. Furthermore we can define where logs from different classes inside our application will get routed with match directive. For example logs from CustomerResource class will be exposed through standard output. 
```
<source>
    @type forward
    @id input1
    @label @mainstream
    port 24224
</source>
<filter **>
    @type stdout
</filter>

<label @mainstream>
    <match com.kumuluz.ee.samples.kumuluzee_logs.CustomerResource.**>
        @type file
        @id output2
        path "/fluentd/log/data.com.kumuluz.ee.samples.kumuluzee_logs.CustomerResource.*.log"
        symlink_path "/fluentd/log/data.log"
        append true
    </match>
    <match docker.**>
        @type file
        @id output_docker
        path "/fluentd/log/docker.*.log"
        symlink_path "/fluentd/log/docker.log"
        append true
        time_slice_format %Y%m%d
        <buffer time>
          timekey_wait 1m
          timekey 86400
          path /fluentd/log/docker.*.log
        </buffer>
      </match>
      <match **>
          @type file
          @id output_other
          path "/fluentd/log/data.*.log"
          symlink_path "/fluentd/log/data.log"
          append true
          time_slice_format %Y%m%d
          <buffer time>
            timekey_wait 10m
            timekey 86400
            path /fluentd/log/data.*.log
          </buffer>
      </match>
</label>
```

### Run Fluentd daemon
Confuration file is available in the project as Customers.conf we are going to copy it to `/tmp`.
```bash
cp Customers.conf /tmp
```
Then we can run the Daemon inside Docker and mount the `/fluentd/log`
```bash
docker run --name fluentd-daemon -d -p 24224:24224 -p 24224:24224/udp -v /tmp:/fluentd/log -v /tmp:/fluentd/etc fluent/fluentd:v1.3-debian-1 -c /fluentd/etc/Customers.conf
```

### Add KumuluzEE Logs support

Enhance `CustomerResource` class by adding KumuluzEE Logs annotations:

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
@Log(LogParams.METRICS)
public class CustomerResource {

    ...

    @POST
    @Log(value = LogParams.METRICS, methodCall = false)
    public Response addNewCustomer(Customer customer) {
        Database.addCustomer(customer);
        return Response.noContent().build();
    }
}
```

### Add Fluentd logging configuration

The location of Fluentd daemon can be speficied through configuration. Here we will set properties inside config.yaml.

In this sample in directory `resources` create file `config.yaml`:

```yaml
kumuluzee:
  name: fluentd-sample
  env:
    name: dev
  version: 1.0.0
  logs:
    fluentd:
      address: localhost
      port: 24224
```
### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
### Check the logs
Logs are avaiable in `/fluentd/log/` directory inside Docker container and were mounted on `/tmp` directory on your computer. 
