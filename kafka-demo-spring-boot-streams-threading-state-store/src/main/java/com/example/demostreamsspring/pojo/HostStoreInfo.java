package com.example.demostreamsspring.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostStoreInfo {

    private String host;

    private int port;

    private Set<String> storeNames;

}
