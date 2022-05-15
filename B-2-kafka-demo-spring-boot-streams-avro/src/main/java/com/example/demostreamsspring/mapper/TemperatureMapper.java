package com.example.demostreamsspring.mapper;

import com.example.demostreamsspring.Temperature;
import com.example.demostreamsspring.pojo.TemperaturePojo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TemperatureMapper {

    TemperaturePojo toPojo(Temperature temperature);

}
