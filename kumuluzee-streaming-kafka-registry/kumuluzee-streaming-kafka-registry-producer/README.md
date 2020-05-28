# Kafka producer with Schema Registry

This sample's focus is on using Schema Registry with Kafka. For Kafka basics, see the `kumuluzee-streaming-kafka` sample.

> After initial project build, you should cd to kumuluzee-streaming-kafka-registry-producer to run the avro and schema registry plugins from the producer module.

## Additional maven dependencies
We need Avro and record serializer:
```
<dependency>
    <groupId>org.apache.avro</groupId>
    <artifactId>avro</artifactId>
</dependency>
<dependency>
    <groupId>io.confluent</groupId>
    <artifactId>kafka-avro-serializer</artifactId>
</dependency>
```

## Components
1. Confluent Schema Registry for schema management and validation
2. Landoop Schema Registry UI for Schema Registry visuals
3. Kafdrop UI to check Kafka topics, consumers and records
4. `avro-maven-plugin` to generate .java POJOs from .avsc schemas
5. `kafka-schema-registry-maven-plugin` to register, validate and download schemas.

## Typical development flow (producer)
1. Write an .avsc schema, example in `src/main/resources/schemas/avro/v1.0.0/pricing.avsc`.
2. Generate .java POJO with `avro-maven-plugin` (with `mvn compile`)
3. Use the generated POJO in Kafka Producer (`ProducerResource.java`).
4. Register the schema to registry with `mvn schema-registry:register`. The schema should appear in the registry. We can verify this by opening the Registry UI at `http://localhost:8000`.

Producer configuration is the same as regular Kafka producer with added schema registry URL and disabling automatic registration of schemas. We use `KafkaAvroSerializer` to serialize POJO to Avro data format.
```
producer-avro:
    bootstrap-servers: localhost:29092
    key-serializer: org.apache.kafka.common.serialization.StringSerializer
    value-serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
    schema-registry-url: http://localhost:8081
    auto-regiter-schemas: false
```

## Try it
Run the producer EeApplication and make a POST request with some data:
```
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"price":"0.00","priceExTax":"0.00","taxAmount":"0.00","basePrice":"0.00","priceAmount":"0.00","description":"Desc"}' \
  http://localhost:8080/produce
```
Now navigate to Kafdrop at `http://localhost:9000/`, open `pricing-avro` topic and click on __View Messages__ and then again __View Messages__. You should see the content of your message displayed as a plain text.

## Schema evolution
If you want to modify the scheme, first copy the .avsc to a new file. An example is given in `v2.0.0/pricing.avsc` with one required and one optional field added. __BACKWARD__ compatibility is the default so according to the [compatibility guide](https://docs.confluent.io/current/schema-registry/avro.html) we are allowed to delete fields and add optional fields. That means our new schema is not compatible and should fail. 
First, in our producer pom.xml we replace the `v1.0.0/pricing.avsc` with the new `v2.0.0/pricing.avsc` under the `kafka-schema-registry-maven-plugin` pricing-avro-value subject.

We can then test the compatibility with:
```
mvn schema-registry:test-compatibility
```
Results in:
> [ERROR] Execution default-cli of goal io.confluent:kafka-schema-registry-maven-plugin:5.5.0:test-compatibility failed: One or more schemas found to be incompatible with the current version.

However, if we remove the new required field from our 2.0.0 schema and re-run the compatibility test, we get an OK message:
> [INFO] Schema pricing-2.0.0.avsc is compatible with subject(pricing-avro-value)

We could now change our Avro Maven Plugin source directory to `v2.0.0`, generate the new POJO and register the new schema.

Now that we have successfully produced a record, continue to the [consumer module](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-streaming-kafka-registry/kumuluzee-streaming-kafka-registry-consumer) README.
