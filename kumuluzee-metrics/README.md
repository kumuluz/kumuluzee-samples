# KumuluzEE Metrics sample

> Build a REST service, pack it as a KumuluzEE microservice, and monitor its performance with KumuluzEE Metrics

The objective of this sample is to demonstrate how to use the built-in monitoring framework to expose basic 
runtime metrics.

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
    $ cd kumuluzee-metrics
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
* JAX-RS REST resource - http://localhost:8080/v1/customers
* Metrics JSON & Prometheus - http://localhost:8080/metrics

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to use KumuluzEE Metrics and pack the application as a KumuluzEE
microservice. We will extend the existing
[KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs).
Therefore, first complete the existing JAX-RS sample tutorial, or clone the JAX-RS sample code.

We will follow these steps:
* Complete the tutorial for
[KumuluzEE JAX-RS REST sample](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) or clone the existing
sample
* Add Maven dependencies
* Add metrics collectors (counters, meters, timers,...)
* Add configuration
* Add Web Application Instrumentation
* Configure the servlet
* Configure the Logstash reporter
* Configure the Logs reporter
* Build the microservice and run it

### Add Maven dependencies

Since your existing starting point is the existing KumuluzEE JAX-RS REST sample, you should already have the
dependencies for `kumuluzee-bom`, `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-jax-rs-jersey` configured
in `pom.xml`.

Add the `kumuluzee-cdi-weld`, `kumuluzee-config-mp` and `kumuluzee-metrics-core` dependencies:
```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-cdi-weld</artifactId>
</dependency>
<dependency>
    <groupId>com.kumuluz.ee.config</groupId>
    <artifactId>kumuluzee-config-mp</artifactId>
    <version>${kumuluzee-config-mp.version}</version>
</dependency>
<dependency>
    <groupId>com.kumuluz.ee.metrics</groupId>
    <artifactId>kumuluzee-metrics-core</artifactId>
    <version>${kumuluzee-metrics.version}</version>
</dependency>
```

### Add metrics colectors (counters, meters, timers,...)

Let's first define some monitoring tools to collect a few of the metrics in our `CustomerResource` class. We'll add two
meters, that monitor the rate at which the customers are being added and removed. We define them either by annotating
the method with `@Metered`, or by creating a `Meter` object and then calling the `mark()` method every time the add or
remove methods are being called. Let's also measure the time it takes for the method to execute. We can do that by
adding the `@Timed` annotation to the method. We'll also add two more tools in this example, a counter and a histogram.
Here is a code snippet of the example resource bean, which illustrates the usage of mentioned tools:

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
@RequestScoped
public class CustomerResource {

    @Inject
    @Metric(name = "customer_counter")
    private ConcurrentGauge customerCounter;

    @Inject
    @Metric(name = "first_name_length_histogram")
    private Histogram nameLength;

    @Inject
    @Metric(name = "customer_adding_meter")
    private Meter addMeter;

    @GET
    public Response getAllCustomers() {
        List<Customer> customers = Database.getCustomers();
        getCustomerCount();
        return Response.ok(customers).build();
    }

    @GET
    @Path("{customerId}")
    public Response getCustomer(@PathParam("customerId") int customerId) {
        Customer customer = Database.getCustomer(customerId);
        if(customer != null) {
            return Response.ok(customer).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("add-sample-names")
    @Timed(name = "add-sample-names-timer")
    public Response addSampleNames() {
        addNewCustomer(new Customer(Database.getCustomers().size(), "Daniel", "Ornelas"));
        addNewCustomer(new Customer(Database.getCustomers().size(), "Dennis", "McBride"));
        addNewCustomer(new Customer(Database.getCustomers().size(), "Walter", "Wright"));
        addNewCustomer(new Customer(Database.getCustomers().size(), "Mitchell", "Kish"));
        addNewCustomer(new Customer(Database.getCustomers().size(), "Tracy", "Edwards"));

        return Response.noContent().build();
    }

    @POST
    public Response addNewCustomer(Customer customer) {
        addMeter.mark();
        customerCounter.inc();
        nameLength.update(customer.getFirstName().length());
        Database.addCustomer(customer);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{customerId}")
    @Metered(name = "customer_deleting_meter")
    public Response deleteCustomer(@PathParam("customerId") int customerId) {
        customerCounter.dec();
        Database.deleteCustomer(customerId);
        return Response.noContent().build();
    }

    @Gauge(name = "customer_count_gauge", unit = MetricUnits.NONE)
    private int getCustomerCount() {
        return Database.getCustomers().size();
    }
}
```

### Add configuration

Most of the metrics components can be configured though configuration. 
Here is a sample config that sets a few service parameters:

```yaml
kumuluzee:
  name: metrics-sample
  version: 1.0.0
  environment:
    name: dev
```

### Add web application instrumentation

Let's add web instrumentation, that monitors requests and responses at a certain url. We will define two endpoints;
one monitoring `/metrics` endpoint and one monitoring `/v1/customers` endpoint. We can do that by simply adding the 
following lines to the config file:

```yaml
kumuluzee:
    metrics:
        web-instrumentation:
          - name: metricsEndpoint
            url-pattern: /metrics/*
          - name: customersEndpoint
            url-pattern: /v1/customers/*
```

### Configure the servlet

It's time to look at the metrics we collected. The servlet for exporting metrics is already built in and enabled by 
default in development environment or in debug mode. The metrics can be seen at http://localhost:8080/metrics. The 
servlet can also be disabled or remapped in the config by setting the `kumuluzee.metrics.servlet.enabled` and 
`kumuluzee.metrics.servlet.mapping` respectively.

Servlet exposes the following endpoints:
- GET /metrics - All collected metrics. If the `Accept` header is set to `application/json`, servlet returns metrics in
  JSON format. Otherwise, servlet returns metrics in Prometheus format.
- OPTIONS /metrics - Metadata about the collected metrics.

Here is an example output of GET request on the `/metrics` endpoint with the `Accept` header set to `application/json`:
```json
{
    "application": {
        "com.kumuluz.ee.samples.kumuluzee_metrics.CustomerResource.add-sample-names-timer": {
            "count": 3,
            "meanRate": 0.01609796426559552,
            "oneMinRate": 0.029872241020718428,
            "fiveMinRate": 0.32928698165641596,
            "fifteenMinRate": 0.4912384518467888,
            "min": 156867,
            "max": 3457480,
            "mean": 1271679,
            "stddev": 1545698.2505290825,
            "p50": 200690,
            "p75": 3457480,
            "p95": 3457480,
            "p98": 3457480,
            "p99": 3457480,
            "p999": 3457480
        },
        "com.kumuluz.ee.samples.kumuluzee_metrics.CustomerResource.customer_deleting_meter": {
            "count": 1,
            "meanRate": 0.05720811542737377,
            "oneMinRate": 0.16929634497812282,
            "fiveMinRate": 0.1934432200964012,
            "fifteenMinRate": 0.19779007785878447
        },
        "com.kumuluz.ee.samples.kumuluzee_metrics.CustomerResource.customer_count_gauge": 14,
        "com.kumuluz.ee.samples.kumuluzee_metrics.CustomerResource.customer_adding_meter": {
            "count": 15,
            "meanRate": 0.08048839766795181,
            "oneMinRate": 0.1493612051035921,
            "fiveMinRate": 1.6464349082820788,
            "fifteenMinRate": 2.456192259233944
        },
        "com.kumuluz.ee.samples.kumuluzee_metrics.CustomerResource.customer_counter": 14,
        "com.kumuluz.ee.samples.kumuluzee_metrics.CustomerResource.first_name_length_histogram": {
            "count": 15,
            "min": 5,
            "max": 8,
            "mean": 6.199999999999999,
            "stddev": 0.9797958971132713,
            "p50": 6,
            "p75": 6,
            "p95": 8,
            "p98": 8,
            "p99": 8,
            "p999": 8
        }
    },
    "base": {
        "memory.committedHeap": 304611328,
        "thread.daemon.count": 12,
        "gc.PS-MarkSweep.count": 1,
        "classloader.totalLoadedClass.count": 4402,
        "thread.count": 18,
        "gc.PS-Scavenge.count": 3,
        "classloader.totalUnloadedClass.count": 0,
        "memory.maxHeap": 3713531904,
        "gc.PS-Scavenge.time": 42,
        "gc.PS-MarkSweep.time": 44,
        "memory.usedHeap": 34963072,
        "jvm.uptime": 200420
    }
}
```

As you can see, our custom defined metrics are reported in the `application` registry. Metrics about the JVM, memory
usage and other system metrics are automatically collected and reported in the `base` registry.

More information about the metrics can be acquired by making an OPTIONS request on the `/metrics` endpoint with the
`Accept` header set to `application/json`. Here is an example output:

```json
{
    "application": {
        "com.kumuluz.ee.samples.kumuluzee_metrics.CustomerResource.add-sample-names-timer": {
            "unit": "nanoseconds",
            "type": "counter",
            "description": "",
            "displayName": "",
            "tags": ""
        },
        "com.kumuluz.ee.samples.kumuluzee_metrics.CustomerResource.customer_deleting_meter": {
            "unit": "per_second",
            "type": "counter",
            "description": "",
            "displayName": "",
            "tags": ""
        },
        ...
    },
    "base": {
        "memory.committedHeap": {
            "unit": "bytes",
            "type": "gauge",
            "description": "Displays the amount of memory in bytes that is committed for the Java virtual machine to use. This amount of memory is guaranteed for the Java virtual machine to use.",
            "displayName": "Committed Heap Memory",
            "tags": ""
        },
        "thread.count": {
            "unit": "none",
            "type": "counter",
            "description": "Displays the current number of live threads including both daemon and non-daemon threads",
            "displayName": "Thread Count",
            "tags": ""
        },
        "jvm.uptime": {
            "unit": "milliseconds",
            "type": "gauge",
            "description": "Displays the start time of the Java virtual machine in milliseconds. This attribute displays the approximate time when the Java virtual machine started.",
            "displayName": "JVM Uptime",
            "tags": ""
        },
        ...
    }
}
```

### Configure the Logs reporter

A reporter that automatically reports metrics to the available logging framework can be configured.
Lets first add a dependency:

```xml
<dependency>
    <groupId>com.kumuluz.ee.metrics</groupId>
    <artifactId>kumuluzee-metrics-logs</artifactId>
    <version>${kumuluzee-metrics.version}</version>
</dependency>
```

You can set it up in the configuration by enabling it (`kumuluzee.metrics.logs.enabled`) and defining the logging
level (`kumuluzee.metrics.logstash.level`) and the time period (`kumuluzee.metrics.logstash.period-s`).

### Configure the Logstash reporter

A reporter for automatically reporting metrics to Logstash can be configured. Lets first add a dependency:

```xml
<dependency>
    <groupId>com.kumuluz.ee.metrics</groupId>
    <artifactId>kumuluzee-metrics-logstash</artifactId>
    <version>${kumuluzee-metrics.version}</version>
</dependency>
```

You can set it up in the configuration by enabling it (`kumuluzee.metrics.logstash.enabled`) and defining the address 
(`kumuluzee.metrics.logstash.address`), the port (`kumuluzee.metrics.logstash.port`) and the time period 
(`kumuluzee.metrics.logstash.period-s`).

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
