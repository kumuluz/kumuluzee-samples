# KumuluzEE Java Server Faces (JSF) sample

> Develop a sample JSF application and pack it as a KumuluzEE microservice.

The objective of this sample is to demonstrate how to develop a JSF application and pack it as a KumuluzEE microservice. The tutorial guides you through the development of a JSF application, including the development of a managed bean, JSF views, and configuring the web module. It shows how to pack a JSF application as a microservice. Required knowledge: basic familiarity with JSF.

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
    $ cd jsf
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
* JSF page - http://localhost:8080

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a servlet web module (with JSF 2.2) and pack it as a KumuluzEE microservice. 
We will develop a simple front-end in JSF and required back-end with the following functionalities:
* User interface and logic to add a customer
* User interface and logic to list all customer
* User interface with welcome page and list of options

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.).
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet, JSP, JSF).
* Implement the front-end using JSF views
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

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty`, `kumuluzee-jsp-jetty` and `kumuluzee-jsf-mojarra` dependencies:
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
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
        <artifactId>kumuluzee-jsf-mojarra</artifactId>
    </dependency>
</dependencies>
```

Add the `maven-dependency-plugin` build plugin to copy all the necessary dependencies into target folder:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <version>2.10</version>
            <executions>
                <execution>
                    <id>copy-dependencies</id>
                    <phase>package</phase>
                    <goals>
                        <goal>copy-dependencies</goal>
                    </goals>
                    <configuration>
                        <includeScope>runtime</includeScope>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Implement backend beans

First, let us create a `Customer` POJO:

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
Implement the `CustomerBean` class. This class will serve as a bean supporting functionalities of the user interface. Note that `@SessionScoped` and `@ManagedBean` are both imported from the package `javax.faces.bean`. If needed, you can modify the code to use CDI beans and CDI scopes. In that case, you need to add the file `beans.xml` to the directory `resources/META-INF` file and add the `kumuluzee-cdi-weld` dependency to pom.xml. Sample for CDI configuration can be found in [KumuluzEE JPA sample documentation](https://github.com/kumuluz/kumuluzee-samples/tree/master/jpa).

Implementation of `CustomerBean` class:

```java
@ManagedBean
@SessionScoped
public class CustomerBean implements Serializable {

    private String id;
    private String firstName;
    private String lastName;

    public CustomerBean() {
    }

    public CustomerBean(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // TODO: implement getters and setters

    public void addCustomer() {
        Customer customer = new Customer();
        customer.setId(this.getId());
        customer.setFirstName(this.getFirstName());
        customer.setLastName(this.getLastName());

        Database.addCustomer(customer);

        this.setId(null);
        this.setFirstName(null);
        this.setLastName(null);
    }

    public List<Customer> getCustomers()
    {
        return Database.getCustomers();
    }

    @Override
    public String toString() {
        return "Customer {\n" +
                "  id='" + id + "',\n" +
                "  firstName='" + firstName + "',\n" +
                "  lastName='" + lastName + "'\n" +
                "}";
    }
}
```
### Implement the views

Create the directory `resources/webapp` and add the views `input.xhtml`, `list.xhtml` and `welcome.xhtml`.

**welcome.xhtml**
```xhtml
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://java.sun.com/jsf/html">

<h:head>
    <title>KumuluzEE JSF sample</title>
</h:head>
<h:body>
    <h2>Choose an action:</h2>
    <h:form>
        <h:commandButton value="Add customer" action="input"/>
        <br/>
        <br/>
        <h:commandButton value="List customers" action="list"/>
    </h:form>
</h:body>
</html>
```

**list.xhtml**
```xhtml
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:ui="http://java.sun.com/jsf/facelets">

<h:head>
    <title>KumuluzEE JSF sample</title>
</h:head>
<h:body>
    <h2>Customers</h2>
    <ui:repeat value="#{customerBean.customers}" var="customer">
        <h:outputText value="#{customer.toString()}"/>
        <br/>
    </ui:repeat>
    <br />
    <h:form>
        <h:commandButton value="Home" action="welcome"/>
    </h:form>
</h:body>
</html>
```

**input.xhtml**
```xhtml
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://java.sun.com/jsf/html">

<h:head>
    <title>KumuluzEE JSF sample</title>
</h:head>
<h:body>
    <h2>Please enter customer data below:</h2>
    <h:form>
        <h:outputLabel for="id">
            ID:
            <h:inputText id="id" value="#{customerBean.id}"/>
        </h:outputLabel>
        <br/>
        <h:outputLabel for="firstName">
            First name:
            <h:inputText id="firstName" value="#{customerBean.firstName}"/>
        </h:outputLabel>
        <br/>
        <h:outputLabel for="lastName">
            Last name:
            <h:inputText id="lastName" value="#{customerBean.lastName}"/>
        </h:outputLabel>
        <br/>
        <br/>
        <h:commandButton value="Add" action="#{customerBean.addCustomer()}"/>
        <br/>
        <h:commandButton value="Home" action="welcome"/>
    </h:form>
</h:body>
</html>
```

### Configure web module
In the directory `resources/webapp/WEB-INF` add the file `web.xml`:

```xml
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1" xmlns="http://xmlns.jcp.org/xml/ns/javaee">

    <display-name>KumuluzEE JSF sample</display-name>

    <!-- Welcome page -->
    <welcome-file-list>
        <welcome-file>welcome.xhtml</welcome-file>
    </welcome-file-list>

    <!-- JSF mapping -->
    <servlet>
        <servlet-name>Faces Servlet</servlet-name>
        <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <!-- Map these files with JSF -->
    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.jsf</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.faces</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.xhtml</url-pattern>
    </servlet-mapping>

    <listener>
        <listener-class>com.sun.faces.config.ConfigureListener</listener-class>
    </listener>
</web-app>
```

### Build the sample and run it

To build the sample and run the example, use the commands as described in previous sections.
