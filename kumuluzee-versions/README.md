# KumuluzEE Versions example

This sample demonstrates how to use KumuluzEE Versions.

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

This sample is based on the [KumuluzEE JAX-RS sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs),
with only a few new dependencies, plugins and files.

## Usage

The example uses maven to build and run the microservice.

1. Build the sample using maven:

    ```bash
    $ cd jax-rs
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
* The versions endpoint will be by default accessible at http://localhost:8080/versions

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will show you how to enable KumuluzEE Versions.

### Add Maven dependencies

Dependencies are the same as in the jax-rs example with the addition of:
```xml
<dependencies>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
        <artifactId>kumuluzee-json-p-jsonp</artifactId>
    </dependency>
    <dependency>
        <groupId>com.kumuluz.ee.versions</groupId>
        <artifactId>kumuluzee-versions</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### Plugins

We will be using the maven resources plugin to get the group_id, maven_version etc. 
and also the maven buildnumber plugin, to get the git commit id. We only need to specify the buildnumber plugin in pom.xml

```xml
<build>
    <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>buildnumber-maven-plugin</artifactId>
        <version>${buildnumber-maven-plugin.version}</version>
        <executions>
            <execution>
                <phase>validate</phase>
                <goals>
                    <goal>create</goal>
                </goals>
            </execution>
        </executions>
        <configuration>
            <doCheck>false</doCheck>
            <doUpdate>false</doUpdate>
        </configuration>
    </plugin>
</build>
```

You can set doCheck to true, to make building fail if there are any locally modified files. Setting doUpdate to true
will update your local repository from remote before building.

### Setting resources folder

We have to set the resources folders, so that the plugins know in which folders to look for files that have fields that
need to be filled.

```xml
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>false</filtering>
        </resource>
        <resource>
            <directory>src/main/resources/META-INF</directory>
            <filtering>true</filtering>
            <includes>
                <include>VERSIONS.json</include>
            </includes>
            <targetPath>META-INF</targetPath>
        </resource>
    </resources>
</build>
```

Note that if you have a custom name of the VERSIONS.json file, you also have to change it here.

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.