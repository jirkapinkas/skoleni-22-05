package com.example.demostreamsspring;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Slf4j
@Configuration
public class TopicConfiguration {

    @Bean
    public NewTopic temperaturesTopic() {
        return TopicBuilder.name(Topics.TEMPERATURES)
                .partitions(20)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic sensorsTopic() {
        return TopicBuilder.name(Topics.SENSORS)
                .partitions(20)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic lowTemperaturesTopic() {
        return TopicBuilder.name(Topics.LOW_TEMPERATURES)
                .partitions(20)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic highTemperaturesTopic() {
        return TopicBuilder.name(Topics.HIGH_TEMPERATURES)
                .partitions(20)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tmpTemperaturesTopic() {
        return TopicBuilder.name(Topics.TMP_TEMPERATURES)
                .partitions(20)
                .replicas(1)
                .build();
    }

}
