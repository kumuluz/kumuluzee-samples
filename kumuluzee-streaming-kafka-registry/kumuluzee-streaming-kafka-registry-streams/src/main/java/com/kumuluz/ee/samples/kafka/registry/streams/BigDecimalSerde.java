package com.kumuluz.ee.samples.kafka.registry.streams;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.math.BigDecimal;
import java.util.Map;

public class BigDecimalSerde implements Serde<BigDecimal> {

    @Override
    public Serializer<BigDecimal> serializer() {
        return new BigDecimalSerializer();
    }

    @Override
    public Deserializer<BigDecimal> deserializer() {
        return new BigDecimalDeserializer();
    }
}
