# KumuluzEE Samples

> These samples demonstrate how to get started using KumuluzEE. They provide small, specific, working samples that can be used as a reference for your own projects.

These samples and quickstarts contain several working projects that demonstrate how to use [KumuluzEE](https://github.com/kumuluz/kumuluzee). They also serve
as test projects for the framework itself.

We recommend that you go through some of these examples to get a better understanding of the framework and use them
as a reference for your own projects.

Keep in mind that while projects containing multiple microservices are located in the same repository in order to simplify things,
is is often recommended that you separate microservices by repository as well.

Samples will be constantly added over time.

## About

The samples demonstrate many different use cases for using KumuluzEE to create self-sustaining packages. The latest
version of the samples will always use the latest version of KumuluzEE. Therefor it is recommended when running the
examples that you use the latest version in order to use the latest features of the framework. Refer to the usage section
on how to build and run the examples.

The examples are tagged as well. The tags (eg. `v2.0.0`) will correspond to the KumuluzEE release version in order to easily access the
desired version of the framework that is used in the examples. Tha `master` branch will always use the latest snapshot version of the framework
and the latest examples.

If you wish to use a snapshot version of KumuluzEE when running the examples, you must make sure you add the Sonatype snapshots
repository to your `pom.xml`. The `master` branch already contains the repository as it's targeted against the snapshot
version of the framework.

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

We recommend that use the tagged examples as the latest ones may not be finished.

The following examples are available:

- sample1

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
    cd kumuluzee-examples
    git checkout v2.0.0
    ```
    
To run a specific example please refer to the specific README file of the example as each one can be a bit different.
Most of the time you either build and run it directly with a maven command or build Docker containers and run them.

## Changelog

Recent changes can be viewed on Github on the [Releases Page](https://github.com/kumuluz/kumuluzee-samples/releases)

## Contribute

See the [contributing docs](https://github.com/kumuluz/kumuluzee-samples/blob/master/CONTRIBUTING.md)

When submitting an issue, please follow the [guidelines](https://github.com/kumuluz/kumuluzee-samples/blob/master/CONTRIBUTING.md#bugs).

Issues related to KumuluzEE itself should be submitted at https://github.com/kumuluz/kumuluzee/issues.

## License

MIT