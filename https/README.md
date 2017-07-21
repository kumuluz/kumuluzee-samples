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

In order to run this sample you will have to setup the following environment variables:
- __KEYSTORE_PATH__: \<path_to_project\>/src/main/resources/keystore.jks
- __KEYSTORE_PASSWORD__: changeit
- __ENABLE_SSL__: true

If you wish to force SSL, you have to set the following environment variable:
- __FORCE_SSL__: true

You can also specify a different port for HTTPS:
- __SSL_PORT__: \<ssl_port\>

## Usage

The example uses maven to build and run the microservice.

1. Build the sample using maven:

    ```bash
    $ cd https
    $ mvn clean package
    ```

2. Run the sample:

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
