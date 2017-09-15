# KumuluzEE websocket sample

> Develop a websocket endpoint using standard WebSocket 1.1 API and pack it as a KumuluzEE microservice.

The objective of this sample is to show how to develop a websocket endpoint using standard WebSocket 1.1 API and pack it as a KumuluzEE microservice. The tutorial will guide you through the necessary steps. You will add KumuluzEE dependencies into pom.xml. To develop the websocket endpoint, you will use the standard WebSocket 1.1 API. 
Required knowledge: basic familiarity with websockets.

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
    $ cd websocket
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
* HTML interface - http://localhost:8080
* Websocket endpoint - ws://localhost:8080/customer

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to create a simple websocket endpoint with a simple web user interface using standard WebSocket 1.1 API and pack it as a KumuluzEE microservice. 
We will develop a simple customer websocket endpoint, that returns a greeting for a customer.

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components (Core, Servlet and WebSocket)
* Implement the websocket endpoint using standard WebSocket 1.1 API
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

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-websocket-jetty` dependencies:
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
         <artifactId>kumuluzee-websocket-jetty</artifactId>
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

### Implement the websocket endpoint

Implement the websocket example like so:

```java
@ServerEndpoint("/customer")
public class CustomerEndpoint {

    @OnMessage
    public String greetCustomer(String name) {
        System.out.print("Preparing greeting for customer '" + name + "' ...");
        return "Hello, " + name + "!";
    }
}
```

### Implement the web user interface

Create the directory `resources/webapp` and add the view `index.html` and the controller `websocket.js`.

**welcome.xhtml**
```html
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Websocket sample</title>
</head>
<body>
    <h1>Websocket sample</h1>
    <div style="text-align: center;">
        <form action="">
            <table>
                <tr>
                    <td>
                        Responses
                        <br/>
                        <textarea readonly="true" rows="6" cols="50" id="responseField"></textarea>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input id="textField" name="name" type="text" placeholder="Enter name ...">
                        <input onclick="sendMessage();" value="Send" type="button">
                    </td>
                </tr>
            </table>
        </form>
    </div>
    <br/>
    <div id="output"></div>
    <script language="javascript" type="text/javascript" src="websocket.js"></script>
</body>
```

**websocket.js**
```javascript
var wsUri = "ws://" + document.location.hostname + ":" + document.location.port + document.location.pathname + "customer";
var websocket = new WebSocket(wsUri);

websocket.onopen = function(evt) { onOpen(evt) };
websocket.onmessage = function(evt) { onMessage(evt) };
websocket.onerror = function(evt) { onError(evt) };
var output = document.getElementById("output");

function sendMessage() {
    websocket.send(textField.value);
}

function onOpen() {
    writeToScreen("Connected to " + wsUri);
}

function onMessage(event) {
    console.log("onMessage: " + event.data);
    responseField.innerHTML += event.data + "\n";
}

function onError(event) {
    writeToScreen('<span style="color: red;">ERROR:</span> ' + event.data);
}

function writeToScreen(message) {
    output.innerHTML += message + "<br>";
}
```

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
