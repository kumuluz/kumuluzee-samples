# KumuluzEE Event Streaming samples using Kafka

> Samples for KumuluzEE Event Streaming Kafka project with Schema Registry

This module contains three samples that will introduce you to KumuluzEE
Event Streaming project using Kafka and Schema Registry:

- [`kumuluzee-streaming-kafka-registry-producer`](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-streaming-kafka-registry/kumuluzee-streaming-kafka-registry-producer) Kafka message producer microservice that produces messages using Avro schema and Confluent Schema Registry
- [`kumuluzee-streaming-kafka-registry-consumer`](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-streaming-kafka-registry/kumuluzee-streaming-kafka-registry-consumer) Kafka message consumer microservice that consumes messages using Avro schema and Confluent Schema Registry
- [`kumuluzee-streaming-kafka-streams`](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-streaming-kafka-registry/kumuluzee-streaming-kafka-registry-streams) Kafka Streams microservice for stream processing using Avro schema and Confluent Schema Registry

More information about the samples can be found in the README of each sample.

You can use the docker-compose.yaml in root of this project to start all the necessary Kafka containers. Start with `docker-compose up -d`.

You should start with the producer README, followed by the consumer README and finally the streams README.

## What is Kafka Schema Registry and why should I use it?
Schema Registry allows us to keep record data consistent and compatible between updates and reduces the possibility of breaking consumers when producers start producing modified data. Without schema registry, if developer wants to remove, add or modify the record fields in any way, he or she must carefully consider how the change will effect the downstream consumers and whether or not the change is breaking for the consumers.

With Schema Registry, we can set a compatibility level for each topic and registry will prevent us from registering any new schema that breaks that compatibility.

## Additional Notes
Confluent dependencies are only available through the Confluent Maven Repository which is configured in root pom of this project.
