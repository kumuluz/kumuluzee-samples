# KumuluzEE GraphQL advanced sample
> Unleash the full power of GraphQL extension.

This is an advanced tutorial, which demonstrates advanced extension usage. Please finish the [basic tutorial](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-graphql-jpa-simple) first.

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
```
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
* GraphiQL endpoint - http://localhost:8080/graphiql (playground for executing queries)

To shut down the example simply stop the processes in the foreground.

You can test the example by running the [following queries](queries.txt).

## Detailed code explanation
In this section, we will cover some of the use cases in this example and explain how they work.

### Using pagination, sorting and filtering without JPA optimized queries
This is demonstrated in the sample with method allStudents in StudentResolvers.class. Even though we are using JPA in this case, the same would apply for a normal list, which we would have gotten from some other source and not from the database. But because GraphQLUtils.process accepts List (origin doesn't matter), this is irrelevant.

```java
@GraphQLQuery
public PaginationWrapper<Student> allStudents(@GraphQLArgument(name="pagination") Pagination pagination, @GraphQLArgument(name="sort") Sort sort, @GraphQLArgument(name="filter") Filter filter) {
    return GraphQLUtils.process(facultyBean.getStudentList(), pagination, sort, filter);
}
``` 
In this method, we call facultyBean.getStudentList (facultyBean is CDI injected bean) to get a list of all students. 
That list is then passed to GraphQLUtils along with pagination, sort and filter objects.
Process method returns PaginationWrapper, which contains processed list and pagination metadata.
Pagination, Sort and Filter objects are optional. If you don't need the functionality, it's recommended to omit the parameters.
This works like Plug&Play. Just add the parameter to the method and you will add the functionality.
Example query:
```
query StudentsNormal {
  allStudents(pagination: {offset: 0, limit: 10}, sort: {fields: [{field: "studentNumber", order: ASC}]}, filter: {fields: [{op: NIN, value: "[Harry]", field: "name"}]}) {
    result {
      name
      ...studentFields
      subjects {
        name
        classroom
      }
    }
    pagination {
      offset
      limit
      total
    }
  }
}

fragment studentFields on Student {
  studentNumber
  surname
}

```
You can see, that the parameters of the method have translated to input parameters in the graphql query. We also demonstrated the use of so called graphql fragments. 
Check [here](http://graphql.org/learn/queries/#fragments) to read more about them.
Nested sort was also added in the subjects (each student has a list of subjects), which we'll explain in the next section.
This query produces result:

```json
{
  "data": {
    "allStudents": {
      "result": [
        {
          "name": "James",
          "studentNumber": 63170000,
          "surname": "Smith",
          "subjects": [
            {
              "name": "Maths",
              "classroom": "P22"
            },
            {
              "name": "Programming in Java",
              "classroom": "PA"
            }
          ]
        },
        {
          "name": "John",
          "studentNumber": 63170001,
          "surname": "Johnson",
          "subjects": [
            {
              "name": "Programming in Python",
              "classroom": "P1"
            }
          ]
        },
        {
          "name": "Robert",
          "studentNumber": 63170002,
          "surname": "Williams",
          "subjects": []
        }
      ],
      "pagination": {
        "offset": 0,
        "limit": 10,
        "total": 3
      }
    }
  }
}
```

### Nested processsing
As we already mentioned in the previous section, nested processing is also supported and works the same as root level processing. 
If your root fields contain another list, that list can also be filtered, paginated and sorted. 
This can be achieved by creating a new function (has to be annotated with `GraphQLQuery`), that has parent class injected with `GraphQLContext` and has other parameters such as Sort in our example.
If your parent object already has the list you want, you can just call process function with the list. 
If not, you can access the parent type and get all its fields and perform the required logic.


```java
// data is from internal source (for example JPA) (this example is not in the sample; only for demonstration)
@GraphQLQuery
public List<Subject> subjects(@GraphQLContext Student student, @GraphQLArgument(name="sort") Sort sort) {
  return GraphQLUtils.processWithoutPagination(student.getSubjects(), sort);
}
  
// data is from external source
@GraphQLQuery
public List<Subject> subjects(@GraphQLContext Student student, @GraphQLArgument(name="sort") Sort sort) {
    List<Subject> subjectList = subjectBean.getSubjects(student.getStudentNumber());
    return GraphQLUtils.processWithoutPagination(subjectList, sort);
}
```
We also demonstrated the usage of function processWithoutPagination, which does the same thing as process but does not paginate data. It only accepts Sort and/or Filter objects. It returns a `List<Type>`.

> If we want, we can also use pagination in nested processing. 
Just because it wasn't used in this example, doesn't mean that it's not supported. 
If we did use it, we would use the process method instead.

### Using pagination, sorting and filtering with JPA optimized queries
Getting the whole list from the database and then processing data (adding pagination, sorting and filtering) is an expensive operation if our database has a lot of data.
We can solve this, by using JPAUtils from [kumuluzee-rest](https://github.com/kumuluz/kumuluzee-rest) extension, which is directly integrated into GraphQLUtils.
If you use JPA in your project, this is the recommended approach (will use less resources).
There are two ways of doing that:

#### Let GraphQLUtils do all the heavy lifting
This is a simpler approach. Method process (which we already mentioned before) has integrated JPAUtils call, if we pass EntityManager and our class as parameter. Other parameters are the same (Pagination, Sort and Filter).

```java
public List<Lecturer> getLecturerList(Pagination pagination, Sort sort) {
    return GraphQLUtils.process(entityManager, Lecturer.class, pagination, sort).getResult();
}
```

#### Call JPAUtils manually
If you want to use JPAUtils manually, you can also do that. It requires are few lines of code more, but it offers more flexibility.

```java
public PaginationWrapper<Lecturer> getLecturerListManually(Pagination pagination, Sort sort, Filter filter) {
    QueryParameters queryParameters = GraphQLUtils.queryParameters(pagination, sort, filter);
    List<Lecturer> subjectList = JPAUtils.queryEntities(entityManager, Lecturer.class, queryParameters);
    Long count = JPAUtils.queryEntitiesCount(entityManager, Lecturer.class, queryParameters);
    return GraphQLUtils.wrapList(subjectList, pagination, count.intValue());
}
```

Firstly, we need to convert our Pagination, Sort and Filter objects to QueryParameters (which JPAUtils accept).
After that, we can call JPAUtils methods.
If we want to add count to our PaginationWrapper, we also need to provide it manually when wrapping our list. 
In this case we used another JPAUtils call (which is called automatically if you use the process method).
If you don't provide count, it will default to null when querying.


### Omiting pagination metadata
If we want to have more clear structure result in graphql queries and don't want to use `PaginationWrapper<Type>` for any reason, we can omit it.
We can achieve this, by calling getResult() on a PaginationWrapper object, which will return a normal processed list. The data will be still paginated.

```java
public List<Lecturer> getLecturerList(Pagination pagination, Sort sort) {
    return GraphQLUtils.process(entityManager, Lecturer.class, pagination, sort).getResult();
}
```
This example is not annotated, because it is not a part of LecturerResolvers.class but is a part of our CDI bean. This method is called in LecturerResolvers.class here:

```java
@GraphQLQuery
public List<Lecturer> allLecturers(@GraphQLArgument(name="pagination") Pagination pagination, @GraphQLArgument(name="sort") Sort sort) {
    return facultyBean.getLecturerList(pagination, sort);
}
```

### Injecting PersistenceContext into Resolver class
As you can see, we only called getLecturerList method in our LecturerResolvers class (in the previous example). 
We can eliminate unneccesarry code by injecting PersistenceContext to our class (AssistantResolvers in this example) and call GraphQLUtils directly.

```java
@PersistenceContext
private EntityManager em;

@GraphQLQuery
public List<Assistant> allAssistants(@GraphQLArgument(name="sort") Sort sort) {
    return GraphQLUtils.processWithoutPagination(em, Assistant.class, sort);
}
```

### Example mutation with query parameters
Mutations are also a key part of using apis (PUT/POST/DELETE requests in standard REST apis).
```java
@GraphQLMutation
public Student addStudent(@GraphQLArgument(name="student") Student student) {
    ctx.addStudent(student);
    return student;
}
```
The following Java code will produce a mutation with input type StudentInput (output types can't be used as inputs in graphql).
StudentInput type will contain the same fields as the output, but you can ignore fields or create custom types.
The field becomes ignored in input, if you put a @GraphQLIgnore annotation on a setter.
In our example, we don't want to allow adding enrolled date directly when creating a student, because we will set enrolled date manually.
Empty constructor is also required in order to create an object from deserialization.

```java
public Student() {
    
}

@GraphQLIgnore
public void setEnrolled(Date enrolled) {
    this.enrolled = enrolled;
}
```
We can perform the following call with this mutation:
```
mutation mutation1 {
  addStudent(student: {studentNumber: 63170004, name: "Test", surname: "Test"}) {
    name
  }
}

mutation mutation2($st: StudentInput) {
  addStudent(student: $st) {
    studentNumber
    name
    surname
  }
}

// this needs to be passed as query parameters in graphql because of $st variable defined above
{
  "st": {
    "studentNumber": 63170005,
    "name": "SomeName",
    "surname": "SomeSurname"
  }
}
```

These mutations will produce the following result:

```json
{
  "data": {
    "addStudent": {
      "name": "Test"
    }
  }
}
  
{
  "data": {
    "addStudent": {
      "studentNumber": 63171342,
      "name": "Latest",
      "surname": "Greatest"
    }
  }
}
```
