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

package com.kumuluz.ee.samples.kafka.streams;

import com.kumuluz.ee.streaming.common.annotations.StreamProcessor;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;

/**
 * @author Matija Kljun
 */
@ApplicationScoped
public class WordCountStreamsBuilder {

    @StreamProcessor(id = "word-count", autoStart = false)
    public StreamsBuilder wordCountBuilder() {

        StreamsBuilder builder = new StreamsBuilder();

        // Serializers/deserializers (serde) for String and Long types
        final Serde<String> stringSerde = Serdes.String();

        // Construct a `KStream` from the input topic "streams-plaintext-input", where message values
        // represent lines of text (for the sake of this example, we ignore whatever may be stored
        // in the message keys).
        KStream<String, String> textLines = builder.stream("in",
                Consumed.with(stringSerde, stringSerde));

        KTable<String, String> wordCounts = textLines
                // Split each text line, by whitespace, into words.
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                // Group the text words as message keys
                .groupBy((key, value) -> value)
                // Count the occurrences of each word (message key).
                .count()
                .mapValues((key, value) -> value.toString());

        // Store the running counts as a changelog stream to the output topic.
        wordCounts.toStream().to("out", Produced.with(stringSerde, stringSerde));

        return builder;

    }
}