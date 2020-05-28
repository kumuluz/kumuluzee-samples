# Kafka Streams with Schema Registry

This sample's focus is on using Schema Registry with Kafka. For Kafka basics, see the `kumuluzee-streaming-kafka` sample.

> After initial project build, you should cd to kumuluzee-streaming-kafka-registry-streams to run the avro and schema registry plugins from the streams module.

## Additional maven dependencies
We need Avro and record serializer:
```
<dependency>
    <groupId>org.apache.avro</groupId>
    <artifactId>avro</artifactId>
</dependency>
<dependency>
    <groupId>io.confluent</groupId>
    <artifactId>kafka-streams-avro-serde</artifactId>
</dependency>
```

# Tutorial
Our streams application will behave like a consumer and producer at the same time. We will consume prices from `pricing-avro` topic, sum the `price` field and produce the new sum into `sum-avro` topic using a sum schema.

## Generate Sum POJO and register schema
Our schema resides in `src/main/resources/schemas/avro/sum.avsc`. Generate POJO with `mvn compile`.
POJO appears in `src/generated` folder.

Now register the schema with `mvn schema-registry:download`.

## Download Pricing schema and generate POJO
For the consumer part, we need to pull down the latest schema and generate the POJO:
```
mvn schema-registry:download
mvn compile
```
Pricing.java also appears in `src/generated` folder.

## Test
After running streams EeApplication, insert a new price, this time with a price value of 1.00 instead of the previous 0.00.

```
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"price":"1.00","priceExTax":"0.00","taxAmount":"0.00","basePrice":"0.00","priceAmount":"0.00","description":"Desc"}' \
  http://localhost:8080/produce
```
Now check the `sum-avro` topic in Kafdrop. It should contain a record with sum __1.00__.

Produce another price, this time with a value of __15.00__.
```
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"price":"15.00","priceExTax":"0.00","taxAmount":"0.00","basePrice":"0.00","priceAmount":"0.00","description":"Desc"}' \
  http://localhost:8080/produce
```
Refresh Kafdrop and you should see a new record with value of __16.00__.

## Conclusion
This sample has demonstrated how to use Avro schemas with Kafka and Confluent Schema Registry. It shows typical use cases and flows for consumer, producers and streams.