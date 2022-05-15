package com.example.demostreamsspring;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Random;

@Slf4j
@Component
public class Example1Component {

    private boolean producerRunning = true;

    @Bean
    public NewTopic temperaturesTopic() {
        return TopicBuilder.name(Topics.TEMPERATURES)
                .partitions(10)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic sensorsTopic() {
        return TopicBuilder.name(Topics.SENSORS)
                .partitions(10)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic outputTopic() {
        return TopicBuilder.name(Topics.OUTPUT_TOPIC)
                .partitions(20)
                .replicas(1)
                .build();
    }

    @Autowired
    private KafkaTemplate<String, Temperature> temperatureTemplate;

    @Autowired
    private KafkaTemplate<String, Sensor> sensorTemplate;

    private final Random random = new Random();

    // Tady je Producer
    @EventListener(ApplicationReadyEvent.class)
    public void afterStartup() throws InterruptedException {
        Sensor sensorBasement = Sensor.newBuilder()
                .setName("sensor_A")
                .setLocation("Basement")
                .build();
        sensorTemplate.send(new ProducerRecord<>(Topics.SENSORS, "sensor_A", sensorBasement));
        Sensor sensorKitchen = Sensor.newBuilder()
                .setName("sensor_B")
                .setLocation("Kitchen")
                .build();
        sensorTemplate.send(new ProducerRecord<>(Topics.SENSORS, "sensor_B", sensorKitchen));
        while (producerRunning) {
            var value1 = Temperature.newBuilder()
                    .setName("sensor_A")
                    .setVal(random.nextInt(100))
                    .build();
            temperatureTemplate.send(new ProducerRecord<>(Topics.TEMPERATURES, "sensor_A", value1));
            var value2 = Temperature.newBuilder()
                    .setName("sensor_B")
                    .setVal(random.nextInt(100))
                    .build();
            // Priklad na posilani vlastiho casu
//            long timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 24); // aktualni cas minus jeden den
            // partition = null => Kafka si zvoli partition
//            temperatureTemplate.send(new ProducerRecord<>(Topics.TEMPERATURES, null, timestamp, "sensor_B", value2));
            temperatureTemplate.send(new ProducerRecord<>(Topics.TEMPERATURES, "sensor_B", value2));
            log.info("produced sensor values: A = {}, B = {}", value1.getVal(), value2.getVal());
            Thread.sleep(1000);
        }
    }

    @PreDestroy
    public void destroy() {
        producerRunning = false; // end producer while loop
    }

    // Tohle je Consumer
//    @KafkaListener(id = "my_group_id", topics = "input_topic")
//    public void listen(Temperature value) {
//        log.info("rec.: {}", value);
//    }

}
