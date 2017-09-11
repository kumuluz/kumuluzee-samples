# KumuluzEE Discovery &mdash; discover services in servlet 

> Develop a KumuluzEE servlet that discovers a microservice registered with etcd.

The objective of this sample is to show how to discover a service, registered with etcd using KumuluzEE Discovery 
extension. The tutorial will guide you through the necessary steps. You will add KumuluzEE dependencies into pom.xml.
You will develop a simple servlet, which uses KumuluzEE Discovery extension for discovering registered services. 
Required knowledge: basic familiarity with servlets and basic familarity with etcd.

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
    $ cd discovery-samples/discovery-discover-servlet
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
* Servlet - http://localhost:8080/DiscoverServlet

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a servlet, which uses KumuluzEE Discovery extension.
We will develop a simple servlet with the following resources:
* GET http://localhost:8080/DiscoverServlet - discover resource and send it a simple request

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet)
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

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty`, `kumuluzee-cdi-weld` and `kumuluzee-discovery` dependencies:
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
        <artifactId>kumuluzee-cdi-weld</artifactId>
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

Implement the servlet, which will return a response, received from discovered service:

```java
@WebServlet("DiscoverServlet")
public class DiscoverServlet extends HttpServlet {

    @Inject
    private DiscoveryUtil discoveryUtil;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        URL url = discoveryUtil.getServiceInstance("customer-service", "*", "dev", AccessType.DIRECT).orElse(null);

        if (url != null) {
            response.getWriter().println("Discovered instance on " + url);

            response.getWriter().println("Sending request for customer list ...");
            URL serviceUrl = new URL(url.toString() + "/v1/customers");
            HttpURLConnection conn = (HttpURLConnection) serviceUrl.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder receivedResponse = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                receivedResponse.append(line);
            }
            rd.close();

            response.getWriter().println("Received response: " + receivedResponse.toString());

        } else {
            response.getWriter().println("No service instances were discovered.");
        }
    }
}
```

In the example above, we inject an `URL` resource using `@DiscoverService` annotation. KumuluzEE Discovery extension
uses NPM-like versioning, so by specifying version "*", we always get the latest version of a microservice, registered with etcd.
Servlet sends a GET request to the discovered URL and sends back the received response.

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