package com.example.demostreamsspring.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AverageTemperaturePojo {

    private double val;

    private String dt;

}
