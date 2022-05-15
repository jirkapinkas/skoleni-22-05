package com.example.demostreamsspring.controller;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Profile("streams_app")
@RequiredArgsConstructor
//@RestController
public class StateStoreController {

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    // http://localhost:8181/temperatures/extreme/count/sensor_A
    // http://localhost:8181/temperatures/extreme/count/sensor_B
    @GetMapping(path = "/temperatures/extreme/count/{sensorName}"/*, produces = "application/json"*/)
    public long temperaturesExtremeCount(@PathVariable String sensorName) {
        ReadOnlyKeyValueStore<Object, Object> store = streamsBuilderFactoryBean.getKafkaStreams().store(StoreQueryParameters.fromNameAndType("extremeTemperaturesCount", QueryableStoreTypes.keyValueStore()));
        return (Long) store.get(sensorName);
    }

}
