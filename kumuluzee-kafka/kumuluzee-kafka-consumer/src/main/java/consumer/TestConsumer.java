package consumer;

import com.kumuluz.ee.kafka.annotations.KafkaListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class TestConsumer {

    @KafkaListener(topics = {"test"})
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            System.out.printf("@KafkaListener - cosnumer - offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
