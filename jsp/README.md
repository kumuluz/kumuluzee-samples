# KumuluzEE Java Server Pages (JSP) and Servlet sample

> Develop a sample JSP and Servlet and pack it as a KumuluzEE microservice.

The objective of this sample is to demonstrate how to develop JSP and Servlets and pack it as a KumuluzEE microservice. The tutorial guides you through the development of a JSP/Servlet application, including the development of a servlet, three different JPSs, and configuring the web module. It shows how to pack JSPs and Servlets as a microservice. Required knowledge: basic familiarity with JSP and Servlets.

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
    $ cd jsp
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
* JSP page - http://localhost:8080

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a sample with Java EE JSP technologies and pack it as a KumuluzEE microservice. 
We will develop a simple front-end in JSP and required back-end with the following functionalities:
* User interface and logic to add a customer
* User interface and logic to list all customer
* User interface with welcome page and list of options

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.).
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet, JSP).
* Implement the front-end using JSP views
* Implement supporting backend beans
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

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty`, `kumuluzee-jsp-jetty` dependencies:
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
        <artifactId>kumuluzee-jsp-jetty</artifactId>
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

### Implement backend beans

First, let us create a `Customer` Java POJO:

```java
public class Customer {

    private String id;
    private String firstName;
    private String lastName;

    // TODO: add getters and setters
}
```

Create `Database` class to simulate persistence layer:

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
Implement `CustomerServlet` class. This class will implement a servlet and will handle POST request to add new Customer.

```java
@WebServlet("/customers")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Customer> customers = Database.getCustomers();
        for(Customer customer : customers) {
            response.getWriter().write(customer.toString() + "<br/>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        Customer customer = new Customer();
        customer.setId(id);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);

        Database.addCustomer(customer);

        response.sendRedirect("input.jsp");
    }
}
```
### Implement the views

Create directory `resources/webapp` and add views `input.jsp`, `list.jsp` and `welcome.jsp`.

**welcome.jsp**
```xhtml
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>KumuluzEE JSP sample</title>
</head>
<body>
    <h2>Choose an action:</h2>
    <a href="input.jsp">Add customer</a>
    <br/>
    <br/>
    <a href="list.jsp">List customers</a>
</body>
</html>
```

**list.jsp**
```xhtml
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>KumuluzEE JSF sample</title>
</head>
<body>
    <h2>Customers</h2>
    <jsp:include page="${pageContext.request.contextPath}/customers"/>
</body>
</html>
```

**input.jsp**
```xhtml
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>KumuluzEE JSF sample</title>
</head>
<body>
    <h2>Please enter customer data below:</h2>
    <form action="${pageContext.request.contextPath}/customers" method="post">
        <label for="id">ID:
            <input type="text" id="id" name="id"/>
        </label>
        <label for="firstName">First name:
            <input type="text" id="firstName" name="firstName"/>
        </label>
        <label for="lastName">Last name:
            <input type="text" id="lastName" name="lastName"/>
        </label>
        <br/>
        <br/>
        <input type="submit" id="submit" name="submit" value="Add"/>
        <br/>
        <a href="welcome.jsp">Home</a>
    </form>
</body>
</html>
```

### Configure web module
In directory `resources/webapp/WEB-INF` add file `web.xml`:

```xml
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1" xmlns="http://xmlns.jcp.org/xml/ns/javaee">

    <display-name>KumuluzEE JSP sample</display-name>

    <!-- Welcome page -->
    <welcome-file-list>
        <welcome-file>welcome.jsp</welcome-file>
    </welcome-file-list>
</web-app>
```

### Build the sample and run it

To build the sample and run the example, use the commands as described in previous sections.
