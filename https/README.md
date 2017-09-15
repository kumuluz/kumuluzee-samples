# KumuluzEE JAX-RS REST service over HTTPS sample

> Develop a REST service using standard JAX-RS 2 API, expose it over HTTPS and pack it as a KumuluzEE microservice.

The objective of this sample is to show how to develop a REST service using standard JAX-RS 2 API, expose it over HTTPS 
and pack it as a KumuluzEE microservice. The tutorial will guide you through the necessary steps. You will add KumuluzEE 
dependencies into pom.xml. To develop the REST service, you will use the standard JAX-RS 2 API. Required knowledge: 
basic familiarity with JAX-RS 2 and basic concepts of REST and JSON.

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

In order to run this sample you will have to set the following configuration parameters:
- __kumuluzee.server.https.keystore-path__: \<path_to_project\>/src/main/resources/keystore.jks
- __kumuluzee.server.https.keystore-password__: changeit
- __kumuluzee.server.https.key-password__: changeit
- __kumuluzee.server.https.enabled__: true

The default HTTPS port is 8443. To specify a different port use:
- __kumuluzee.server.https.ssl-port__: \<ssl_port\>

Other TLS/SSL configuration properties:
- __kumuluzee.server.https.http2__: true
- __kumuluzee.server.https.key-aliase__: test
- __kumuluzee.server.https.ssl_protocols__: TLSv1.1,TLSv1.2
- __kumuluzee.server.https.ssl-ciphers__: > 
        TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384,
        TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,
        TLS_DHE_RSA_WITH_AES_128_CBC_SHA256

More details [KumuluzEE TLS/SSL support](https://github.com/kumuluz/kumuluzee/wiki/TLS-SSL-support).

## Usage

The example uses maven to build and run the microservice.

1. Build the sample using maven:

    ```bash
    $ cd https
    $ mvn clean package
    ```

2. Run the sample:
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
* JAX-RS REST resource:
    - http://localhost:8080/v1/customers
    - https://localhost:\<ssl_port\>/v1/customers

To shut down the example simply stop the processes in the foreground.

##Tutorial

We will follow these steps:
* Add maven dependencies
* Implement microservice
* Build
* Run microservice

### Add maven dependencies

To implement microservice add the following dependencies
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
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-jdk14</artifactId>
            <version>1.7.25</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.25</version>
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
 
 ### Build the microservice and run it
 
 To build the microservice and run the example, use the commands as described in previous sections.
