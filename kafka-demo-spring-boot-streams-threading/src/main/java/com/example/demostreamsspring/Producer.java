package com.example.demostreamsspring;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Random;

@Profile("producer_app")
@Slf4j
@Component
public class Producer {

    private boolean producerRunning = true;

    private static final int SENSOR_COUNT = 10;

    @Autowired
    private KafkaTemplate<String, Temperature> temperatureTemplate;

    @Autowired
    private KafkaTemplate<String, Sensor> sensorTemplate;

    private final Random random = new Random();

    // Tady je Producer
    @EventListener(ApplicationReadyEvent.class)
    public void afterStartup() throws InterruptedException {
        for (int i = 0; i < SENSOR_COUNT; i++) {
            Sensor sensorBasement = Sensor.newBuilder()
                    .setName("sensor_" + i)
                    .setLocation("Basement")
                    .build();
            sensorTemplate.send(Topics.SENSORS, "sensor_" + i, sensorBasement);
        }
        while (producerRunning) {
            for (int i = 0; i < SENSOR_COUNT; i++) {
                var value = Temperature.newBuilder()
                        .setName("sensor_" + i)
                        .setVal(random.nextInt(100))
                        .build();
                temperatureTemplate.send(Topics.TEMPERATURES, "sensor_" + i, value);
            }
            temperatureTemplate.flush();
            Thread.sleep(100);
        }
    }

    @PreDestroy
    public void destroy() {
        producerRunning = false; // end producer while loop
    }

}
