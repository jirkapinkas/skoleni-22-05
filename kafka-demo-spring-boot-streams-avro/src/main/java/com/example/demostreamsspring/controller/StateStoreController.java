package com.example.demostreamsspring.controller;

import com.example.demostreamsspring.Stores;
import com.example.demostreamsspring.Temperature;
import com.example.demostreamsspring.mapper.TemperatureMapper;
import com.example.demostreamsspring.pojo.AverageTemperaturePojo;
import com.example.demostreamsspring.pojo.TemperaturePojo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.*;
import org.springframework.http.MediaType;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StateStoreController {

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    private final TemperatureMapper temperatureMapper;

    // http://localhost:8181/temperatures/extreme/count/sensor_A
    // http://localhost:8181/temperatures/extreme/count/sensor_B
    @GetMapping(path = "/temperatures/extreme/count/{sensorName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public long temperaturesExtremeCount(@PathVariable String sensorName) {
        var kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        Objects.requireNonNull(kafkaStreams);
        ReadOnlyKeyValueStore<Object, Object> store = kafkaStreams.store(StoreQueryParameters.fromNameAndType(Stores.EXTREME_TEMPERATURES_COUNT, QueryableStoreTypes.keyValueStore()));
        return (Long) store.get(sensorName);
    }

    // http://localhost:8181/temperatures/max/sensor_A
    // http://localhost:8181/temperatures/max/sensor_B
    @GetMapping(path = "/temperatures/max/{sensorName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TemperaturePojo temperaturesMax(@PathVariable String sensorName) {
        var kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        Objects.requireNonNull(kafkaStreams);
        ReadOnlyKeyValueStore<Object, Object> store = kafkaStreams.store(StoreQueryParameters.fromNameAndType(Stores.MAX_TEMPERATURE, QueryableStoreTypes.keyValueStore()));
        var temperature = (Temperature) store.get(sensorName);
        // Temperature trida je generovana pomoci avro-maven-plugin
        // a Jackson si s ni moc dobre nerozumi, proto tady provadim transformaci na POJO
        return temperatureMapper.toPojo(temperature);
    }

    // http://localhost:8181/temperatures/last-minute/average/sensor_A
    // http://localhost:8181/temperatures/last-minute/average/sensor_B
    @GetMapping(path = "/temperatures/last-minute/average/{sensorName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AverageTemperaturePojo lastAverageTemperature(@PathVariable String sensorName) {
        var kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        Objects.requireNonNull(kafkaStreams);
        ReadOnlyKeyValueStore<Object, ValueAndTimestamp<Object>> store = kafkaStreams.store(StoreQueryParameters.fromNameAndType(Stores.AVERAGE_TEMPERATURE_PER_MINUTE, QueryableStoreTypes.timestampedKeyValueStore()));
        ValueAndTimestamp<Object> valueAndTimestamp = store.get(sensorName);
        var val = Double.parseDouble(valueAndTimestamp.value().toString());
        var timestamp = new Date(valueAndTimestamp.timestamp())
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .plusMinutes(1) // protoze DT je DT posledniho zaznamu v minute a Java Date & Time API neumoznuje zaokrouhlit cas "nahoru"
                .truncatedTo(ChronoUnit.MINUTES)
                .format(DateTimeFormatter.ISO_INSTANT);
        return AverageTemperaturePojo
                .builder()
                .val(val)
                .dt(timestamp)
                .build();
    }

}

