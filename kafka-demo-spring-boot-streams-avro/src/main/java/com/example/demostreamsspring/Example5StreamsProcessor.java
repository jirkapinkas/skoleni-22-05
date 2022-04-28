package com.example.demostreamsspring;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
//@Component
public class Example5StreamsProcessor {

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        final Serde<Temperature> temperatureValueSerde = new SpecificAvroSerde<>();
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", "http://localhost:8081");
        temperatureValueSerde.configure(serdeConfig, false);

        KStream<String, Temperature> messageStream = streamsBuilder
                .stream(Topics.TEMPERATURES, Consumed.with(Serdes.String(), temperatureValueSerde));

        messageStream
                .peek((key, value) -> log.info("processing I. {}", value))
                .repartition(Repartitioned.with(Serdes.String(), temperatureValueSerde))
                .peek((key, value) -> log.info("processing II. {}", value))
                .to(Topics.BAC_TEMPERATURES, Produced.with(Serdes.String(), temperatureValueSerde));

        // Vypise topologii do konzole
        // https://zz85.github.io/kafka-streams-viz/
        Topology topology = streamsBuilder.build();
        log.info("Topology description {}", topology.describe());

    }

}