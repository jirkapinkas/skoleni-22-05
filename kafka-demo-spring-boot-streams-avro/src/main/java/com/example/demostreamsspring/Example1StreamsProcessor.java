package com.example.demostreamsspring;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
//@Component
public class Example1StreamsProcessor {

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        final Serde<Temperature> temperatureValueSerde = new SpecificAvroSerde<>();
        final Serde<Sensor> sensorValueSerde = new SpecificAvroSerde<>();
        final Serde<SensorTemperature> sensorTemperatureValueSerde = new SpecificAvroSerde<>();
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", "http://localhost:8081");
        temperatureValueSerde.configure(serdeConfig, false);
        sensorValueSerde.configure(serdeConfig, false);
        sensorTemperatureValueSerde.configure(serdeConfig, false);

        KTable<String, Sensor> sensorKTable = streamsBuilder
                .table(Topics.SENSORS, Materialized.with(Serdes.String(), sensorValueSerde));

        KStream<String, SensorTemperature> messageStream = streamsBuilder
                .stream(Topics.TEMPERATURES, Consumed.with(Serdes.String(), temperatureValueSerde))
                .join(sensorKTable, (temperature, sensor) -> {
                    return SensorTemperature.newBuilder()
                            .setName(sensor.getName())
                            .setLocation(sensor.getLocation())
                            .setVal(temperature.getVal())
                            .build();
                });
        messageStream.to(Topics.OUTPUT_TOPIC, Produced.with(Serdes.String(), sensorTemperatureValueSerde));
    }

}