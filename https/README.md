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
- __kumuluzee.server.keystore-path__: \<path_to_project\>/src/main/resources/keystore.jks
- __kumuluzee.server.keystore-password__: changeit
- __kumuluzee.server.enable-ssl__: true

If you wish to force SSL, you have to set the following configuration parameter:
- __kumuluzee.server.force-ssl__: true

__NOTE__: this is the preferred setting when in production

The default HTTPS port is 8443. To specify a different port use:
- __kumuluzee.server.ssl-port__: \<ssl_port\>

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
* JAX-RS REST resource:
    - http://localhost:8080/v1/customers
    - https://localhost:\<ssl_port\>/v1/customers

To shut down the example simply stop the processes in the foreground.
