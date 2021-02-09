# KumuluzEE GraphQL advanced sample
> Unleash the full power of GraphQL extension.

This is an advanced tutorial, which demonstrates advanced extension usage. Please finish the
[basic tutorial](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-graphql-jpa-simple) first.

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
In order to run this example you will have to setup a local PostgreSQL database:
- __database host__: localhost:5432
- __database name__: faculty
- __user__: postgres
- __password__: postgres

The required tables will be created automatically upon running the sample.
You can run database inside docker:

```bash
docker run -d --name pg-graphql -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=faculty -p 5432:5432 postgres:latest
```

## Usage
The example uses maven to build and run the microservices.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-graphql-advanced
    $ mvn clean package
    ```

2. Run the sample:
* Uber-jar:

    ```bash
    $ java -jar target/${project.build.finalName}.jar
    ```
    
    in Windows environment use the command
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
* GraphQL endpoint - http://localhost:8080/graphql
* __Graph*i*QL endpoint__ - http://localhost:8080/graphiql (playground for executing queries)

To shut down the example simply stop the processes in the foreground.

## Detailed code explanation

In this section, we will cover some use cases in this example and explain how they work.

### Using pagination, sorting and filtering

The pagination, sorting and filtering are all commonly used patterns in software development, and the plumbing required
to implement these patterns is often repeated between the projects or even within the project itself. To solve this
problem the KumuluzEE GraphQL includes integration with [KumuluzEE REST](https://github.com/kumuluz/kumuluzee-rest) project.

This is demonstrated in the sample with method getStudentsConnection in StudentResolvers.class.
This method uses the Connection pattern, which wraps result list (`edges`) with the pagination metadata (`totalCount`).
You will see this pattern used in multiple places.

```java
@Query
public StudentConnection getStudentsConnection(Long limit, Long offset, String sort, String filter) {

    QueryParameters qp = GraphQLUtils.queryParametersBuilder()
        .withQueryStringDefaults(qsd)
        .withLimit(limit)
        .withOffset(offset)
        .withOrder(sort)
        .withFilter(filter)
        .build();
    
    return studentBean.getStudentConnection(qp);
}
```

There are a few things to note in this method. The first one is the method parameters (`limit`, `offset`, `sort` and `filter`).
They are translated to GraphQL query parameters and are used to construct the `QueryParameters` object. This object is
later used to query the JPA source.

The `QueryStringDefaults` object provides the defaults for the query.
Let's see the producer (defined in the `ApplicationDefaults` class):

```java
@Produces
@ApplicationScoped
public QueryStringDefaults getQueryDefaults() {
    return new QueryStringDefaults()
            .defaultOffset(0)
            .defaultLimit(20)
            .maxLimit(100);
}
```

The `StudentBean` then uses constructed `QueryParameters` to query the JPA source:

```java
public StudentConnection getStudentConnection(QueryParameters qp) {

    return new StudentConnection(JPAUtils.queryEntities(em, Student.class, qp),
            JPAUtils.queryEntitiesCount(em, Student.class, qp));
}
```

One call is needed to query the actual results and one call is needed to query the count. The method then wraps these
two values in the `Connection` object.

To demonstrate the use of this method, let's think of an use-case. Let's say we want all students whose names start
with the letter J. We want to paginate them, so we specify `offset` and `limit` and we want to sort them by
`studentNumber`. In the `filter` parameter we specify our condition, which should be self-explanatory if you used SQL
before. The final query looks like this:

```graphql
query StudentsStartingWithJ {
  studentsConnection(
    offset: "0"
    limit: "10"
    sort: "studentNumber"
    filter: "name:LIKE:J%"
  ) {
    totalCount
    edges {
      studentNumber
      name
      surname
    }
  }
}
```

And the result:

```json
{
  "data": {
    "studentsConnection": {
      "totalCount": 2,
      "edges": [
        {
          "studentNumber": 63170000,
          "name": "James",
          "surname": "Smith"
        },
        {
          "studentNumber": 63170001,
          "name": "John",
          "surname": "Johnson"
        }
      ]
    }
  }
}
```

### Defining queries on types

Another powerful feature of GraphQL is defining queries directly on types. For example a student can have multiple
subjects and the `student -> subject` mapping may not be defined in the same datasource as the students. In our example
they are defined in-memory for simplicity’s sake but in real world, they could be stored in an external cache or
retrieved through external service.

Let's define the query:

```java
@Name("subjects")
public List<Subject> getStudentSubjects(@Source Student student, String sort) {

    QueryParameters qp = GraphQLUtils.queryParametersBuilder()
            .withQueryStringDefaults(qsd)
            .withOrder(sort)
            .build();

    List<Subject> subjectList = subjectBean.getSubjects(student.getStudentNumber());
    return StreamUtils.queryEntities(subjectList, qp);
}
```

Notice the `@Source` annotation, which marks the type we are defining the query on - in our case `Student`. We will also
enable sorting for this query and since the subjects are defined in-memory, we will use `StreamUtils` instead of
`JPAUtils`.

And how do we use this? Let's extend the query above and ask for student's subjects, ordered by their name descending.
The query:

```graphql
query StudentsStartingWithJ {
  studentsConnection(
    offset: "0"
    limit: "10"
    sort: "studentNumber"
    filter: "name:LIKE:J%"
  ) {
    totalCount
    edges {
      studentNumber
      name
      surname
      subjects(sort: "name DESC") {
        id
        name
      }
    }
  }
}
```

And the result:

```json
{
  "data": {
    "studentsConnection": {
      "totalCount": 2,
      "edges": [
        {
          "studentNumber": 63170000,
          "name": "James",
          "surname": "Smith",
          "subjects": [
            {
              "id": 1,
              "name": "Programming in Java"
            },
            {
              "id": 0,
              "name": "Maths"
            }
          ]
        },
        {
          "studentNumber": 63170001,
          "name": "John",
          "surname": "Johnson",
          "subjects": [
            {
              "id": 2,
              "name": "Programming in Python"
            }
          ]
        }
      ]
    }
  }
}
```

To see the real benefit of defining queries on types instead of root let's look at another query which returns the
`Student` type:

```graphql
query StudentById {
  studentById(id: 1) {
    name
    subjects {
      id
      name
    }
  }
}
```

As you can see we can also retrieve student's subjects through this query.

### Mutating the data

Mutations are also a key part of using APIs (REST equivalents of PUT/POST/DELETE requests).
Let's check out an example:

```java
@Mutation
public Student createStudent(@NonNull Student student) {
    return studentBean.createStudent(student);
}
```
The following Java code will produce a mutation with input type StudentInput (output types can't be used as inputs in graphql).
StudentInput type will contain the same fields as the output, but you can ignore fields.
The field becomes ignored in input, if you put an @Ignore annotation on a setter.
In our example, we don't want to allow adding enrolled date directly when creating a student, because we will set
enrolled date manually.

```java
public class Student extends Person {
    
    // ...
    @Ignore
    public void setEnrolled(LocalDate enrolled) {
        this.enrolled = enrolled;
    }
    
}
```

To use the mutation use the following query:

```graphql
mutation CreateStudent {
  createStudent(
    student: {name: "Mike", surname: "Grady", studentNumber: 63170003}
  ) {
    enrolled
    studentNumber
    name
    surname
  }
}

```

These mutations will produce the following result:

```json
{
  "data": {
    "createStudent": {
      "name": "Mike",
      "surname": "Grady",
      "studentNumber": 63170003,
      "enrolled": "2021-02-09"
    }
  }
}
```

## Conclusion

To explore this sample further use the Graph*i*QL served on http://localhost:8080/graphiql and use the __Explorer__ tab
and the __Docs__ tab to try out the remaining queries.
