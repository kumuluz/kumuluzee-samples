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
* Metrics JSON - http://localhost:8080/metrics

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
* Add a config file
* Add Web Application Instrumentation
* Add JVM instrumentation
* Configure a servlet
* Configure Graphite
* Configure Prometheus
* Configure Logstash
* Configure Logs
* Build the microservice and run it

### Add Maven dependencies

Since your existing starting point is the existing KumuluzEE JAX-RS REST sample, you should already have the
dependencies for `kumuluzee-bom`, `kumuluzee-core`, `kumuluzee-servlet-jetty` and `kumuluzee-jax-rs-jersey` configured
in `pom.xml`.

Add the `kumuluzee-cdi-weld` and `kumuluzee-metrics` dependencies:
```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-cdi-weld</artifactId>
</dependency>
<dependency>
    <groupId>com.kumuluz.ee.metrics</groupId>
    <artifactId>kumuluzee-metrics</artifactId>
    <version>${kumuluzee-metrics.version}</version>
</dependency>
```

### Add metrics colectors (counters, meters, timers,...)

Let's first define some monitoring tools to collect a few of the metrics in our `CustomerResource` class. We'll add two
meters, that monitor the rate at which the customers are being added and removed. We define them either by annotating
the method with `@Metered`, or by creating a `Meter` object and then calling the `mark()` method every time the add or
remove methods are being called. Let's also measure the time it takes for the method to execute. We can do that by
adding the `@Timed` annotation to the method. We'll also add two more tools in this example, a counter and a histogram.
Here is a shortened code of the example project, which illustrates the usage of mentioned tools:

```java
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
@ApplicationScoped
public class CustomerResource {

    @Inject
    @Metric(name = "customer_counter")
    private Counter customerCounter;

    @Inject
    @Metric(name = "first_name_length_histogram")
    private Histogram nameLength;

    @Inject
    @Metric(name = "customer_adding_meter")
    private Meter addMeter;

    @GET
    @Path("{customerId}")
    public Response getCustomer(@PathParam("customerId") int customerId) {
        Customer customer = Database.getCustomer(customerId);
        nameLength.update(customer.getFirstName().length());
        ...
    }

    @GET
    @Path("add-sample-names")
    @Timed(name = "add-sample-names-timer")
    public Response addSampleNames() {
        if (getCustomerCount() < 10) {
            ...
            addMeter.mark(5);
            customerCounter.inc(5);
        }
        ...
    }

    @POST
    public Response addNewCustomer(Customer customer) {
        addMeter.mark();
        customerCounter.inc();
       ...
    }

    @Gauge(name = "customer_count_gauge")
    private int getCustomerCount() {
        return Database.getCustomers().size();
    }

    @DELETE
    @Path("{customerId}")
    @Metered(name = "customer_deleting_meter")
    public Response deleteCustomer(@PathParam("customerId") int customerId) {
        customerCounter.dec();
        ...
    }
}
```

### Add a config file

Most of the metrics components can be configured in the configuration file. 
Here is a sample config file that sets a few service parameters:

```yaml
kumuluzee:
  name: metrics-sample
  version: 0.0.1
```

You can also change the name of a default registry, by setting the `kumuluzee.metrics.genericregistryname` parameter. 
The default name is `default`.

### Add web application instrumentation

Let's add the web instrumentation, that monitors requests and responses at a certain url. We will define two endpoints;
one monitoring `/metrics` endpoint and one monitoring `/prometheus` endpoint. We can do that by simply adding the 
following lines to theconfig file:

```yaml
kumuluzee:
    metrics:
        webinstrumentation:
          - name: metrics-endpoint
            urlpattern: /metrics/*
            registryname: default
          - name: prometheus-endpoint
            urlpattern: /prometheus/*
```

### Add JVM instrumentation

The module includes a lot of monitroing tools collecting JVM metrics that can be enabled in the config file. 
You can enable/disable JVM monitoring by setting the `kumuluzee.metrics.jvm.enabled` parameter, which is set to `true` 
by default. You can also change the name of the registry that JVM metrics are grouped under by setting the `kumuluzee
.metrics.jvm.registry` parameter.

### Configure a servlet

It's time to look at the metrics we collected. The servlet for exporting metrics is already built in and enabled by 
default, so the metrics can be seen at http://localhost:8080/metrics. The servlet can also be disabled or remapped in
the config file by setting the `kumuluzee.metrics.servlet.enabled` and `kumuluzee.metrics.servlet.mapping` respectively.

Here is an example output:
```json
{
  "service" : {
    "timestamp" : "2017-07-13T16:12:07.309Z",
    "environment" : "dev",
    "name" : "metrics-sample",
    "version" : "0.0.7",
    "instance" : "instance1",
    "availableRegistries" : [ "jvm", "default", "registry3" ]
  },
  "registries" : {
    "jvm" : {
      "version" : "3.1.3",
      "gauges" : {
        "GarbageCollector.PS-MarkSweep.count" : {
          "value" : 1
        },
        ...
      }
    },
    "default" : {
      "version" : "3.1.3",
      "gauges" : {
        "com.kumuluz.ee.samples.kumuluzee_metrics.CustomerResource.customer_count_gauge" : {
          "value" : 5
        }
      },
      "meters" : {
        "ServletMetricsFilter.metrics-endpoint.responseCodes.ok" : {
          "count" : 2,
          "m15_rate" : 0.002179735240776932,
          "m1_rate" : 0.025690219862652606,
          "m5_rate" : 0.006296838645263653,
          "mean_rate" : 0.0015924774955522764,
          "units" : "events/second"
        },
        ...
        }
    }
}
```

### Configure Prometheus reporter

The reporter can export metrics in the Prometheus format. First add a dependency:
```xml
<dependency>
    <groupId>com.kumuluz.ee.metrics</groupId>
    <artifactId>kumuluzee-metrics-prometheus</artifactId>
    <version>${kumuluzee-metrics.version}</version>
</dependency>
```

The servlet can be disabled or remapped in the config file by setting the `kumuluzee.metrics.prometheus.enabled` and 
`kumuluzee.metrics.prometheus.mapping` respectively.

Prometheus has to be configured to collect the exported metrics.

### Configure Graphite reporter

A reporter for Graphite can be configured. We first add a dependency:
```xml
<dependency>
    <groupId>com.kumuluz.ee.metrics</groupId>
    <artifactId>kumuluzee-metrics-graphite</artifactId>
    <version>${kumuluzee-metrics.version}</version>
</dependency>
```

You can set it up in the config file, by enabling it (`kumuluzee.metrics.graphite.enabled`) and defining its address 
(`kumuluzee.metrics.graphite.address`), the time period (`kumuluzee.metrics.graphite.periods`) and whether or not you 
want to use the [pickle protocol](http://graphite.readthedocs.io/en/latest/feeding-carbon.html#the-pickle-protocol) 
(`kumuluzee.metrics.graphite.pickle`). You can also define graphite port (`kumuluzee.metrics.graphite.port`), otherwise 
the port will be set to `2003` or `2004` if `pickle` is set to true.

### Configure KumuluzEE Logs

A reporter for automatically reporting metrics to the log using KumuluzEE Logs can be configured. Lets first add a dependency:

```xml
<dependency>
    <groupId>com.kumuluz.ee.metrics</groupId>
    <artifactId>kumuluzee-metrics-logs</artifactId>
    <version>${kumuluzee-metrics.version}</version>
</dependency>
```

You can set it up in the config file, by enabling it (kumuluzee.metrics.logs.enabled) and defining the logging level (kumuluzee.metrics.logstash.level) and the time period 
(kumuluzee.metrics.logstash.period-s).

### Configure Logstash reporter

A reporter for automatically reporting metrics to Logstash can be configured. Lets first add a dependency:

```xml
<dependency>
    <groupId>com.kumuluz.ee.metrics</groupId>
    <artifactId>kumuluzee-metrics-logstash</artifactId>
    <version>${kumuluzee-metrics.version}</version>
</dependency>
```

You can set it up in the config file, by enabling it (kumuluzee.metrics.logstash.enabled) and defining the address 
(kumuluzee.metrics.logstash.address), the port (kumuluzee.metrics.logstash.port) and the time period 
(kumuluzee.metrics.logstash.period-s).

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
