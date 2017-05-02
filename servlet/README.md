# KumuluzEE servlet sample

> Servlet usage sample with KumuluzEE

This sample demonstrates how to configure and use Java EE servlets using KumuluzEE.

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

The example uses docker to build and run the microservices.

1. Build the sample using maven:

    ```bash
    $ cd servlet
    $ mvn clean package
    ```

2. Run the sample:

    ```bash
    $ java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```

<!---
2. Run each individual microservice separately (separate terminal) with a single command with the appropriate environment variables that serve as the applications config:
    * `PORT` should containt the port on which the microservice will accept connections
    * `DATABASE_UNIT` should contain the microservice persistence unit (defaults are obtained from the `persistence.xml` file)
    * `DATABASE_URL` should contain the jdbc URL for the persistence unit specified above (defaults are obtained from the `persistence.xml` file)
    * `DATABASE_USER` should contain the database username (defaults are obtained from the `persistence.xml` file)
    * `DATABASE_PASS` should contain the database password (defaults are obtained from the `persistence.xml` file)
    
    ```bash
    $ PORT=3000 java -cp catalogue/target/classes:catalogue/target/dependency/* com.kumuluz.ee.EeApplication
    
    $ PORT=3001 java -cp orders/target/classes:orders/target/dependency/* com.kumuluz.ee.EeApplication
    ```
-->
    
The application/service can be accessed on the following URL:
* Servlet - http://localhost:8080/CustomerServlet

To shut down the example simply stop the processes in the foreground.