# Kafka consumer with Schema Registry

This sample's focus is on using Schema Registry with Kafka. For Kafka basics, see the `kumuluzee-streaming-kafka` sample.

> After initial project build, you should cd to kumuluzee-streaming-kafka-registry-consumer to run the avro and schema registry plugins from the consumer module.

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

## Typical development flow (consumer)
1. Download the latest schema from registry with `mvn schema-registry:download`. A file called `pricing-avro-value.avsc` will appear in `src/main/resources/schemas/avro`.
2. Generate .java POJO with `avro-maven-plugin` (with `mvn compile`)
3. Use the generated POJO in Kafka Consumer (`TestConsumer.java`).

Consumer configuration is the same as regular Kafka consumer with added schema registry URL and enabling `specific-avro-reader` (by default, it deserializes to a generic record). We use `KafkaAvroDeserializer` to deserialize Avro format to POJO.
```
consumer-avro:
    bootstrap-servers: localhost:29092
    group-id: group1
    enable-auto-commit: true
    auto-commit-interval-ms: 1000
    auto-offset-reset: latest
    key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    value-deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
    schema-registry-url: http://localhost:8081
    specific-avro-reader: true
```

## Try it
Run the producer EeApplication and make a POST request with some data:
```
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"price":"0.00","priceExTax":"0.00","taxAmount":"0.00","basePrice":"0.00","priceAmount":"0.00","description":"Desc"}' \
  http://localhost:8080/produce
```
On the consumer side, you should see the message consumed:
> INFO  Consumed message: offset = 3, key = 2b78ede6-9fb5-4f7c-8f35-e0343362c240, value = {"price": "0.00", "priceExTax": "0.00", "basePrice": "0.00", "taxAmount": "0.00", "description": "Desc"}

Finally, you can get the consumed messages via REST API:
```
curl -X GET http://localhost:8079/consume
``` 

## Schema evolution
When producers update their schema, simply pull down the latest schema and rebuild the consumer. Follow the [compatibility guide](https://docs.confluent.io/current/schema-registry/avro.html) for the deployment strategy.

Now that we have successfully consumed a record, continue to the streams module README.
