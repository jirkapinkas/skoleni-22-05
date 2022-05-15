package com.example.kafkademo;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PreDestroy;
import java.util.UUID;

@Slf4j
@SpringBootApplication
public class KafkaDemoApplication {

    private boolean producerRunning = true;

    // Tohle uplne na zacatku vytvori topic s nazvem "topic1"
    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("first_topic")
                .partitions(10)
                .replicas(1)
                .build();
    }

    @Autowired
    private KafkaTemplate<String, String> template;

    // Tady je Producer
    @EventListener(ApplicationReadyEvent.class)
    public void afterStartup() throws InterruptedException {
        while(producerRunning) {
            var value = UUID.randomUUID().toString();
            template.send("first_topic", "test_data", value);
            // Alternativne to same pres ProducerRecord
//            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("first_topic", "test_data", value);
//            template.send(producerRecord);
            log.info("sent: {}", value);
            Thread.sleep(1_000);
        }
    }

    // Tohle je Consumer
    @KafkaListener(id = "my_group_id_1", topics = "first_topic")
    public void listen(String value) {
        log.info("rec 1.: {}", value);
    }

    // Dalsi Consumer
    @KafkaListener(id = "my_group_id_2", topics = "first_topic")
    public void listen(ConsumerRecord<String, String> consumerRecord) {
        log.info("rec 2.: {}", consumerRecord.value());
    }

    @PreDestroy
    public void destroy() {
        producerRunning = false; // end producer while loop
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaDemoApplication.class, args);
    }

}
