package producer;

import com.kumuluz.ee.kafka.annotations.KafkaProducer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TestProducer {

    @Inject
    @KafkaProducer
    private Producer producer;

    public void send(String topic, String msg, String key) {
        ProducerRecord<String,String> record = new ProducerRecord<String,String>( topic, key, msg);

        producer.send(record,
                (metadata, e) -> {
                    if(e != null) {
                        e.printStackTrace();
                    } else {
                        System.out.println("The offset of the produced message record is: " + metadata.offset());
                    }
                });
    }

}