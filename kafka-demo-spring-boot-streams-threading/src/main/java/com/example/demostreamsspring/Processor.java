package com.example.demostreamsspring;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

@Profile("streams_app")
@Slf4j
@Component
public class Processor {

    public void slowdown() {
        try {
            Thread.sleep(new Random().nextInt(50));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        final Serde<Temperature> temperatureValueSerde = new SpecificAvroSerde<>();
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", "http://localhost:8081");
        temperatureValueSerde.configure(serdeConfig, false);

        KStream<String, Temperature> messageStream = streamsBuilder
                .stream(Topics.TEMPERATURES, Consumed.with(Serdes.String(), temperatureValueSerde));

        // topology 0
        messageStream
                .peek((key, value) -> {
//                    log.warn("topology 00 {} {} {}", key. value.getVal(), Thread.currentThread().getName());
                    slowdown();
                })
                .mapValues((readOnlyKey, value) -> {
                    value.setName(value.getName().toUpperCase());
                    return value;
                })
                .to(Topics.TMP_TEMPERATURES, Produced.with(Serdes.String(), temperatureValueSerde));

        // topology 1
        KStream<String, Temperature> messageStream2 = streamsBuilder
                .stream(Topics.TMP_TEMPERATURES, Consumed.with(Serdes.String(), temperatureValueSerde))
                .peek((key, value) -> {
                    log.warn("topology 11 {} {} {}", key, value.getVal(), Thread.currentThread().getName());
                    slowdown();
                })
                .peek((key, value) -> {
                    log.warn("topology 12 {} {} {}", key, value.getVal(), Thread.currentThread().getName());
                    slowdown();
                })                ;

        messageStream2.filter((key, temperature) -> temperature.getVal() < 10)
//                .peek((key, value) -> log.warn("peek {}", Thread.currentThread().getName()))
                .to(Topics.LOW_TEMPERATURES, Produced.with(Serdes.String(), temperatureValueSerde));

        messageStream2
                .filter((key, temperature) -> temperature.getVal() > 90)
//                .peek((key, value) -> log.warn("peek {}", Thread.currentThread().getName()))
                .to(Topics.HIGH_TEMPERATURES, Produced.with(Serdes.String(), temperatureValueSerde));


        // Vypise topologii do konzole
        // https://zz85.github.io/kafka-streams-viz/
        Topology topology = streamsBuilder.build();
        log.info("Topology description {}", topology.describe());

    }

}