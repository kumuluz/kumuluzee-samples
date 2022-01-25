# KumuluzEE migrations with Liquibase

> Create a simple REST service that uses Liquibase for database schema migrations.

The goal of this sample is to demonstrate the use of KumuluzEE Database Schema Migrations with Liquibase. 
The tutorial will walk you through developing database schema migrations at application startup and show you 
how to perform database schema migrations while the application is already running. 
Required knowledge: basic familiarity with JPA, CDI and basic concepts of REST and JSON.

## Requirements

To run this sample, you will need the following:

1. Java 11 (or newer), you can use any implementation:
    * If you have Java installed, you can check the version by typing the following in a command line:
    ```bash
    java -version   
    ```
2. Maven 3.2.1 (or newer):
    * If you have Maven installed, you can check the version by typing the following in a command line:
    ```bash
    mvn -version
    ```
3. Git:
    * If you have Git installed, you can check the version by typing the following in a command line:
    ```bash
    git --version
    ```

## Prerequisites

To run this sample, you need to set up a local PostgreSQL database:

+ **database host:** localhost:5432
+ **database name:** customers
+ **user:** postgres
+ **password:** postgres

The required tables will be created automatically when you run the sample.

You can run the database in a Docker:
```
docker run -d --name books-db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=postgres -p 5432:5432 postgres:latest
```

## Usage

The sample uses Maven to build and run the microservice.

1. Build the sample using Maven:
    ```bash
    cd kumuluzee-migrations-liquibase
    mvn clean package
    ```
2. Start the local PostgreSQL DB:
    ```bash
    docker run -d --name postgres -e POSTGRES_DB=books -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:latest
    ```
3. Run the sample:
    * Uber-jar:
    ```bash
    java -jar target/${project.build.finalName}.jar
    ```

    * Exploded:
    ```bash
    java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```
   
The database tables will be created during tutorial.

The application/service can be accessed via the following URLs:
+ Book endpoints - [http://localhost:8080/v1/books](http://localhost:8080/v1/books)
+ Reset database - [http://localhost:8080/v1/migrations/reset](http://localhost:8080/v1/migrations/reset)
+ Populate database - [http://localhost:8080/v1/migrations/populate](http://localhost:8080/v1/migrations/populate)

To shut down the sample, simply stop the processes in the foreground.

## Tutorial

This tutorial walks you through the steps required to use a Liquibase extension in a KumuluzEE microservice.
Since the JPA and CDI parts are not explained in this tutorial, we recommend that you complete the existing [KumuluzEE JPA and CDI sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jpa)
before proceeding with this one.

We will follow these steps:
+ Add Maven dependencies
+ Create Liquibase changelog
+ Add the Liquibase configuration
+ Implement the REST service to trigger migrations in runtime
+ Build the microservice
+ Run it

### Add Maven dependencies

We need the following dependencies in our microservice:
+ `kumuluzee-core`
+ `kumuluzee-servlet-jetty`
+ `kumuluzee-jax-rs-jersey`
+ `kumuluzee-cdi-weld`
+ `kumuluzee-jpa-eclipselink`
+ `kumuluzee-database-schema-migrations-liquibase`
+ `postgresql`

Add the following Maven dependencies to the `pom.xml`:
```xml
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
   <groupId>com.kumuluz.ee</groupId>
   <artifactId>kumuluzee-cdi-weld</artifactId>
</dependency>
<dependency>
   <groupId>com.kumuluz.ee</groupId>
   <artifactId>kumuluzee-jpa-eclipselink</artifactId>
</dependency>
<dependency>
   <groupId>com.kumuluz.ee.database-schema-migrations</groupId>
   <artifactId>kumuluzee-database-schema-migrations-liquibase</artifactId>
   <version>${kumuluzee-database-schema-migrations.version}</version>
</dependency>

<!-- Only if using PostgreSQL-->
<dependency>
   <groupId>org.postgresql</groupId>
   <artifactId>postgresql</artifactId>
   <version>${postgresql.version}</version>
</dependency>
```

Add the `kumuluzee-maven-plugin` build plugin to package the microservice as uber-jar:
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

### Create Liquibase changelog

This sample already contains a simple `Book` entity for which we will create a Liquibase changelog. 
The changelog will contain two changeSets, one for updating the database table and the other for populating the table.

The changelog file will be named `books-changelog.xml` and will be placed into `resources/db` directory.

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.3.xsd">

    <changeSet author="KumuluzEE" id="create_table_book" context="init">
        <createTable tableName="book">
            <column name="id" type="varchar(128)"/>
            <column name="title" type="varchar(64)"/>
            <column name="author" type="varchar(64)"/>
        </createTable>
    </changeSet>

    <changeSet author="KumuluzEE" id="populate_table_book" context="populate">
        <insert tableName="book">
            <column name="id">2465c7c0-4e43-4dd9-8257-0542d4661b94</column>
            <column name="title">KumuluzEE in action</column>
            <column name="author">KumuluzEE</column>
        </insert>
        <insert tableName="book">
            <column name="id">452aa339-6481-49d4-9024-5796fa6ac633</column>
            <column name="title">KumuluzEE database schema migrations</column>
            <column name="author">KumuluzEE</column>
        </insert>
        <insert tableName="book">
            <column name="id">9c3bb6ce-3906-4a37-b807-229e6687346d</column>
            <column name="title">KumuluzEE tips and tricks</column>
            <column name="author">KumuluzEE</column>
        </insert>
        <insert tableName="book">
            <column name="id">f7c5deb1-1602-41a5-b75d-d23d80f547fc</column>
            <column name="title">KumuluzEE best practices</column>
            <column name="author">KumuluzEE</column>
        </insert>
    </changeSet>

</databaseChangeLog>
```

### Add Liquibase configuration

To trigger the Liquibase migration at application startup, the Liquibase configuration must be placed in 
the KumuluzEE configuration file.
The configuration contains the JNDI name of the data source, the location of the Liquibase changelog file, 
the actions to be performed at startup, the Liquibase contexts and the Liquibase labels.

Add the following configuration:
```yaml
kumuluzee:
  database-schema-migrations:
    enabled: true
    liquibase:
      changelogs:
        - jndi-name: jdbc/BooksDS
          file: db/books-changelog.xml
          contexts: "init"
          startup:
            drop-all: true
            update: true
```

### Implement REST service

To trigger Liquibase database schema migrations at runtime, we need to inject the `LiquibaseContainer` object.
The LiquibaseContainer contains the corresponding Liquibase object, which is created based on the `jndiName` specified 
in the `@LiquibaseChangelog` annotation.

*Note: If only one Liquibase configuration is specified in the KumuluzEE configuration file, 
the `jndiName` parameter or the entire `@LiquibaseChangelog` annotation can be omitted.* 

Sample service:
```java
@RequestScoped
public class LiquibaseService {

    private static final Logger LOG = Logger.getLogger(LiquibaseService.class.getName());

    @Inject
    @LiquibaseChangelog(jndiName = "jdbc/BooksDS")
    private LiquibaseContainer liquibaseContainer;

    public void reset() {

        Liquibase liquibase = liquibaseContainer.createLiquibase();

        // Retrieves contexts and labels from the Liquibase configuration in the KumuluzEE configuration file
        Contexts contexts = liquibaseContainer.getContexts();
        LabelExpression labels = liquibaseContainer.getLabels();

        try {
            liquibase.dropAll();
            liquibase.update(contexts, labels);
            liquibase.validate();

        } catch (Exception e) {
            LOG.error("Error while resetting database.", e);
        }
    }

    public void populate() {

        Liquibase liquibase = liquibaseContainer.createLiquibase();

        try {
            liquibase.update("populate");
        } catch (Exception e) {
            LOG.error("Error while populating database.", e);
        }
    }
}
```

Sample resource:
```java
@Path("migrations")
@RequestScoped
public class LiquibaseResource {

    @Inject
    private LiquibaseService liquibaseService;

    @POST
    @Path("reset")
    public Response reset() {
        liquibaseService.reset();
        return Response.noContent().build();
    }

    @POST
    @Path("populate")
    public Response populate1() {
        liquibaseService.populate();
        return Response.noContent().build();
    }
}
```

### Build the microservice and run it

To build the microservice and run the sample, use the commands described in the previous sections.

After you run the microservice, it should be accessible by default at the URL
[http://localhost:8080/v1](http://localhost:8080/v1).
You can demonstrate the use of the Liquibase extension by following the steps below:

1. Querying the [/books](http://localhost:8080/v1/books) endpoint should result in an empty array,
   since our database is empty.
2. Call the endpoint `http://localhost:8080/v1/migrations/populate` using the method `POST`
   to trigger a database schema migration that populates a database with some sample books.
3. If you query the [/books](http://localhost:8080/v1/books) endpoint again, you should get a response similar to the
   one below, showing us that the migration was successful.
```
[
    {
        "id": "2465c7c0-4e43-4dd9-8257-0542d4661b94",
        "title": "KumuluzEE in action",
        "author": "KumuluzEE"
    },
    {
        "id": "452aa339-6481-49d4-9024-5796fa6ac633",
        "title": "KumuluzEE database schema migrations",
        "author": "KumuluzEE"
    },
    {
        "id": "9c3bb6ce-3906-4a37-b807-229e6687346d",
        "title": "KumuluzEE tips and tricks",
        "author": "KumuluzEE"
    },
    {
        "id": "f7c5deb1-1602-41a5-b75d-d23d80f547fc",
        "title": "KumuluzEE best practices",
        "author": "KumuluzEE"
    }
]
```
4. To clear the entries from the database, call the `http://localhost:8080/v1/migrations/reset` endpoint with 
   a method `POST`. This will also trigger a database schema migration, but this time it will clear the database instead 
   of populating it.
5. If you query the [/books](http://localhost:8080/v1/books) endpoint for the last time, you can see if the migration
   was successful. The expected result is an empty array.