package com.kumuluz.ee.samples.kafka.registry.streams;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.math.BigDecimal;

public class BigDecimalDeserializer implements Deserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(String s, byte[] bytes) {
        return new BigDecimal(new String(bytes));
    }

    @Override
    public BigDecimal deserialize(String topic, Headers headers, byte[] data) {
        return new BigDecimal(new String(data));
    }
}
