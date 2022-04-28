package com.example.demostreamsspring;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

@Slf4j
//@Component
public class Example2StreamsProcessor {

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        final Serde<Temperature> temperatureValueSerde = new SpecificAvroSerde<>();
        final Serde<Sensor> sensorValueSerde = new SpecificAvroSerde<>();
        final Serde<SensorTemperature> sensorTemperatureValueSerde = new SpecificAvroSerde<>();
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", "http://localhost:8081");
        temperatureValueSerde.configure(serdeConfig, false);
        sensorValueSerde.configure(serdeConfig, false);
        sensorTemperatureValueSerde.configure(serdeConfig, false);

        streamsBuilder
                .stream(Topics.TEMPERATURES, Consumed.with(Serdes.String(), temperatureValueSerde))
                .groupByKey()
                .count()
                .mapValues((key, longValue) -> String.valueOf(longValue))
                .toStream()
                .to(Topics.OUTPUT_AGG_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
    }
}