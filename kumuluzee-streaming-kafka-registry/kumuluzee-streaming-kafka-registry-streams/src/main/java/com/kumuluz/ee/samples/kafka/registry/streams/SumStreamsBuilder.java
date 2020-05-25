/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
*/

package com.kumuluz.ee.samples.kafka.registry.streams;

import com.kumuluz.ee.samples.kafka.registry.avro.lib.Pricing;
import com.kumuluz.ee.samples.kafka.registry.avro.lib.Sum;
import com.kumuluz.ee.streaming.common.annotations.StreamProcessor;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * @author Matija Kljun
 */
@ApplicationScoped
public class SumStreamsBuilder {

    @Inject
    private GenericConfig config;

    @StreamProcessor(id="price-sum", autoStart=false, config="streams-avro")
    public StreamsBuilder sum() {

        StreamsBuilder builder = new StreamsBuilder();

        final Serde<String> keySerde = Serdes.String();
        final Serde<BigDecimal> bigDecimalSerde = new BigDecimalSerde();
        final Serde<Pricing> consumeValueSerde = new SpecificAvroSerde<>();
        final Serde<Sum> produceValueSerde = new SpecificAvroSerde<>();

        // config.yml only configures the default SerDes. All non-default SerDes need to be
        // configured explicitely here.
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
            config.getSchemaRegistryUrl());
        consumeValueSerde.configure(serdeConfig, false);
        produceValueSerde.configure(serdeConfig, false);

        final String inputTopic = "pricing-avro";
        final String outputTopic = "sum-avro";

        builder.stream(inputTopic, Consumed.with(keySerde, consumeValueSerde))
            .map((k, v) -> new KeyValue<>("total-sum", new BigDecimal(v.getPrice().toString())))
            .groupByKey(Grouped.with(keySerde, bigDecimalSerde))
            .reduce(BigDecimal::add)
            .toStream()
            .map((k, v) -> new KeyValue<>(k, new Sum(v.toPlainString())))
            .to(outputTopic, Produced.with(keySerde, produceValueSerde));

        return builder;
    }
}