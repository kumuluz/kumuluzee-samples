# KumuluzEE servlet sample

> Develop a servlet using standard Servlet 3.1 API and pack it as a KumuluzEE microservice.

The objective of this sample is to show how to develop a servlet using standard Servlet 3.1 API and pack it as a KumuluzEE microservice. The tutorial will guide you through the necessary steps. You will add KumuluzEE dependencies into pom.xml. To develop the servlet, you will use the standard Servlet 3.1 API. 
Required knowledge: basic familiarity with servlets.

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

The example uses maven to build and run the microservices.

1. Build the sample using maven:

    ```bash
    $ cd servlet
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
     -cp target/classes;target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
The application/service can be accessed on the following URL:
* Servlet - http://localhost:8080/CustomerServlet

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a simple servlet using standard Servlet 3.1 API and pack it as a KumuluzEE microservice. 
We will develop a simple Customer servlet with the following resources:
* GET http://localhost:8080/CustomersServlet - list of all customers

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet)
* Implement the service using standard Servlet 3.1 API
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

Add the `kumuluzee-core` and `kumuluzee-servlet-jetty` dependencies:
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

Implement the servlet, for example, which will return all customers by default on GET request:

```java
@WebServlet("CustomerServlet")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Customer> customers = Database.getCustomers();
        if (customers == null || customers.isEmpty())
            response.getWriter().println("No customers found.");
        else {
            for (Customer customer : customers) {
                response.getWriter().println(customer.toString());
                response.getWriter().println();
            }
        }
    }
}
```

Implement the `Customer` Java class, which is a POJO:
```java
public class Customer {

    private String id;

    private String firstName;

    private String lastName;

    // TODO: implement get and set methods
}
```

In the example above, we use `Database` class to access data. A sample implementation which simulates persistance layer, can be implemented as follows:

```java
public class Database {
    private static List<Customer> customers = new ArrayList<>();

    public static List<Customer> getCustomers() {
        return customers;
    }

    public static Customer getCustomer(String customerId) {
        for (Customer customer : customers) {
            if (customer.getId().equals(customerId))
                return customer;
        }

        return null;
    }

    public static void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public static void deleteCustomer(String customerId) {
        for (Customer customer : customers) {
            if (customer.getId().equals(customerId)) {
                customers.remove(customer);
                break;
            }
        }
    }
}
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
