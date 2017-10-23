# KumuluzEE Samples

> These samples demonstrate how to get started using KumuluzEE microservice framework. They provide small, specific, working samples that can be used as a reference for your own projects.

These samples and quickstarts contain several working projects that demonstrate how to use [KumuluzEE](https://github.com/kumuluz/kumuluzee) microservices. They also serve as test projects for the framework itself.

We recommend that you go through some of these samples to get a better understanding of the framework and use them as a reference for your own projects.

Keep in mind that while projects containing multiple microservices are located in the same repository in order to simplify things, is is often recommended that you separate microservices by repository as well.

Samples will be constantly added over time.

## About

The samples demonstrate many different use cases for using KumuluzEE to create self-sustaining microservices. The latest version of the samples will always use the latest version of the KumuluzEE framework. Therefore, it is recommended to use the latest version of the KumuluzEE framework for these samples. This way, you will also get all the latest features of the KumuluzEE. Refer to the usage section on how to build and run the samples.

Some samples are tagged as well. The tags (eg. `v2.2.0`) will correspond to the KumuluzEE release version in order to easily access the desired version of the framework that is used in the examples. Tha `master` branch will always use the latest snapshot version of the framework and the latest samples.

If you wish to use a snapshot version of KumuluzEE when running the samples, you must make sure you add the Sonatype snapshots repository to your `pom.xml`. The `master` branch already contains the repository as it's targeted against the snapshot version of the framework.

```xml
<repositories>
    <repository>
        <id>sonatype-snapshots</id>
        <name>Sonatype Snapshots</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <releases><enabled>false</enabled></releases>
        <snapshots><enabled>true</enabled></snapshots>
    </repository>
</repositories>
```

The following samples are available (list might not be up-to-date; please refer to the actual list above):

Tutorial:
- Simple microservice tutorial (microservices-simple)
- Cloud-native Java EE Microservices with KumuluzEE: REST service using config, discovery, security, metrics, logging and fault tolerance (tutorial-microservice-config-discovery-faulttolerance-logs-metrics-security)

Java EE samples:
- Bean Validation
- JAX-RS
- JAX-WS
- JPA and CDI
- JSF
- JSP and Servlet
- Servlet
- Websocket
- REST service over HTTPS
- Logging with JUL and KumuluzEE

KumuluzEE extensions - samples:
- KumuluzEE Config
- KumuluzEE Config with etcd
- KumuluzEE Config with Consul
- KumuluzEE MicroProfile Config
- KumuluzEE Discovery with etcd
- KumuluzEE Discovery with Consul
- KumuluzEE Logs with Log4j2
- KumuluzEE Logs with JUL
- KumuluzEE REST
- KumuluzEE Security with Keycloak for REST services
- KumuluzEE Security with Keycloak for CDI
- KumuluzEE Fault Tolerance with Hystrix
- KumuluzEE Event Streaming with Kafka
- KumuluzEE Cors
- KumuluzEE Metrics
- KumuluzEE Health


## Requirements

In order to run these examples as they are intended, you will need the following:

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
        
## Usage

1. Clone the Git repository containing the examples:

    ```
    git clone git@github.com:kumuluz/kumuluzee-samples.git
    ```
    
2. Checkout the desired tagged version of the examples and the KumuluzEE framework (alternatively skip this step if you want the latest and greatest)

    ```
    cd kumuluzee-samples
    git checkout v2.4.0
    ```
    
To run a specific sample, please refer to the specific README file of the sample.
Most of the time you either build and run it directly with a maven command or build Docker containers and run them.

## Changelog

Recent changes can be viewed on Github on the [Releases Page](https://github.com/kumuluz/kumuluzee-samples/releases)

## Contribute

See the [contributing docs](https://github.com/kumuluz/kumuluzee-samples/blob/master/CONTRIBUTING.md)

When submitting an issue, please follow the [guidelines](https://github.com/kumuluz/kumuluzee-samples/blob/master/CONTRIBUTING.md#bugs).

Issues related to KumuluzEE itself should be submitted at https://github.com/kumuluz/kumuluzee/issues.

## License

MIT
