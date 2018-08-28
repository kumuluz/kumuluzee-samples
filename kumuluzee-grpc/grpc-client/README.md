# Kumuluzee gRPC client sample

## Requirements

In order to run this example you will need the following:
1. Java 8 (or newer), you can use any implementation:
    * If you have installed Java, you can check the version by typing the
    following in command line:
    ```bash
   java -version   
    ```
2. Maven 3.2.1 (or newer):
    * If you have installed Maven, you can check the version by typing the
    following in a command line:
    ```bash
    mvn -version
    ```
3. Git:
    * If you have installed Git, you can check the version by typing the
    following in a command line:
    ```bash
    git --version
    ```

## Usage

The example uses maven to build and run the microservice.

1. Build the sample using maven:
    ```bash
    cd kumuluzee-grpc-sample/grpc-client
    mvn clean package
    ```
    
2. Run the sample:
    * Uber-jar:
    ```bash
    java -jar target/${project.build.finalName}.jar
    ```
    
    * Exploded:
    ```bash
    java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```

The grpc [server](../grpc-server) should be running on port and address specified in
the config.yml (`grpc.client.port` and `grpc.client.address`)file. On success the following line
should appear in log output:

```bash
2018-02-26 09:42:26.774 INFO -- client.UserServiceClient$1 -- Primoz Hrovat
2018-02-26 09:42:26.777 INFO -- client.UserServiceClient$1 -- Completed
```

## Tutorial

This tutorial will guide you through the steps required to initialize gRPC client
in KumuluzEE microservice.

### Add Maven dependencies

Add the `kumuluzee-core`, `kumuluzee-servlet-jetty`, `kumuluzee-cdi-weld`,
and `kumuluzee-grpc` dependency to the sample:

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
        <artifactId>kumuluzee-cdi-weld</artifactId>
    </dependency>

    <dependency>
        <groupId>com.kumuluz.ee.grpc</groupId>
        <artifactId>kumuluzee-grpc</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

</dependencies>
```

Add the `kumuluzee-maven-plugin` build plugin to package microservice as uber-jar,
`protobuf-maven-plugin` plugin to generate java classes from `.proto` files and 
`os-maven-plugin` extension to let maven discover your OS so it can download the appropriate
compiler for Protobuf files.

```xml
<build>
    <extensions>
        <extension>
            <groupId>kr.motd.maven</groupId>
            <artifactId>os-maven-plugin</artifactId>
            <version>1.5.0.Final</version>
        </extension>
    </extensions>
    <plugins>
        <plugin>
            <groupId>org.xolstice.maven.plugins</groupId>
            <artifactId>protobuf-maven-plugin</artifactId>
            <version>0.5.1</version>
            <configuration>
                <protocArtifact>com.google.protobuf:protoc:3.5.1-1:exe:${os.detected.classifier}</protocArtifact>
                <pluginId>grpc-java</pluginId>
                <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.14.0:exe:${os.detected.classifier}</pluginArtifact>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>compile</goal>
                        <goal>compile-custom</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
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

If you prefer exploded version change `goal` in `kumuluzee-maven-plugin` to `copy-dependencies`.

### Define proto file

Define `.proto` file with service and messages definition. More about Protobuf files
can be found on [Google Developers](https://developers.google.com/protocol-buffers/). Place your proto files
in "proto" directory so the maven plugin can detect it and compile correspondent Java classes.

```proto
syntax = "proto3";
option java_package = "client";

service User {
    rpc getUser(UserRequest) returns (UserResponse) {};
}

message UserRequest {
    int32 id = 1;
}

message UserResponse {
    int32 id = 1;
    string name = 2;
    string surname = 3;
}
```

### Implement client

Implement stub (client) that establishes channel with provided configuration. To simplify code you can use GrpcClientConf
helper class provided in `kumuluzee-grpc`. Then you can create new request and call it just like any other java method.

```java
@ApplicationScoped
public class UserServiceClient {

    private final static Logger logger = Logger.getLogger(UserServiceClient.class.getName());
    private final String JWT_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0IiwiaWF0IjoxNTE2MjM5MDIyfQ." +
            "VhfWc4uNtTNicztGHvnXyRMhnmIrXZ947EjO9ECV3G6pzPCYqjzwkdTgykW-" +
            "FWbQsSJH6aVnryK0DoLrO8f4XEsblj_Ind1CffXYcqjyZxwkPy4r5SxA--QvewsUsWfC1_I55J-Z6kh7oHm5Z_7vasudOFAXukmY5uBg_adDJN4";
    private UserGrpc.UserStub stub;

    @PostConstruct
    public void init() {
        try {
            GrpcChannels clientPool = GrpcChannels.getInstance();
            GrpcChannelConfig config = clientPool.getGrpcClientConfig("client1");
            GrpcClient client = new GrpcClient(config);
            stub = UserGrpc.newStub(client.getChannel()).withCallCredentials(new JWTClientCredentials(JWT_TOKEN));
        } catch (SSLException e) {
            logger.warning(e.getMessage());
        }
    }

    public void getUser(Integer id) {
        UserService.UserRequest request = UserService.UserRequest.newBuilder()
                .setId(id)
                .build();

        stub.getUser(request, new StreamObserver<UserService.UserResponse>() {
            @Override
            public void onNext(UserService.UserResponse userResponse) {
                logger.info(userResponse.getName() + " " + userResponse.getSurname());
            }

            @Override
            public void onError(Throwable throwable) {
                logger.warning("Error retrieving user");
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                logger.info("Completed");
            }
        });
    }
}
```

### Startup class

Just for demonstration purposes an Example bean is created at startup that calls `UserServiceClient` and it's method.

```java
@ApplicationScoped
public class Example {

    private final static Logger logger = Logger.getLogger(Example.class.getName());

    @Inject
    UserServiceClient userClient;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object o){
        logger.info("Example initialized");
        userClient.getUser(1);
    }
}
```

### Add configuration

Add required configuration:
```yaml
kumuluzee:
  name: "grpc client test"
  server:
    http:
      port: 8081
  grpc:
    clients:
    - name: client1
      port: 8443
      address: localhost
```

### Build the microservice and run it

To build the microservice and run the example use the commands as described in previous section.


