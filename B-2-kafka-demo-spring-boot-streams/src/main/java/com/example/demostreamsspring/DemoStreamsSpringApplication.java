package com.example.demostreamsspring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Random;

@RequiredArgsConstructor
@Slf4j
@EnableKafkaStreams // tohle vytvori bean typu StreamsBuilder
@SpringBootApplication
public class DemoStreamsSpringApplication {

    // Tohle uplne na zacatku vytvori topic s nazvem "input_topic"
    @Bean
    public NewTopic inputTopic() {
        return TopicBuilder.name("input_topic")
                .partitions(10)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic outputTopic() {
        return TopicBuilder.name("output_topic")
                .partitions(10)
                .replicas(1)
                .build();
    }

    private final KafkaTemplate<String, String> template;

    // Tady je Producer
    @EventListener(ApplicationReadyEvent.class)
    public void afterStartup() throws InterruptedException {
        while (true) {
            var value = String.valueOf(new Random().nextInt(100));
            template.send(new ProducerRecord<>("input_topic", null, value));
            log.info("sent: {}", value);
            Thread.sleep(1_000);
        }
    }

    // Tohle je Consumer
    @KafkaListener(id = "my_group_id", topics = "input_topic")
    public void listen(String value) {
        log.info("rec.: {}", value);
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoStreamsSpringApplication.class, args);


    }

}
