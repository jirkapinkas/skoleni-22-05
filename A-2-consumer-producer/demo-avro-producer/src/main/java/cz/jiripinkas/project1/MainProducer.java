package cz.jiripinkas.project1;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class MainProducer {

    public static void main(String[] args) throws InterruptedException {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
//        props.put(ProducerConfig.ACKS_CONFIG, "all");
//        props.put(ProducerConfig.RETRIES_CONFIG, 0);
//        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
//        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
//        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");


        KafkaProducer<String, Movie> producer = new KafkaProducer<>(props);
        while (true) {
            var value = Movie.newBuilder()
                    .setTitle(UUID.randomUUID().toString())
                    .setYear(2022)
                    .setStuff("neco")
                    .build();
            producer.send(new ProducerRecord<>("first_topic", null, value));
            log.info("sent: {}", value);
            Thread.sleep(100);
        }
    }
}
