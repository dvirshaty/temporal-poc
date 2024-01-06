package com.temporal.demos.temporalspringbootdemo.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class HsiaDtoDog {

    private Long id;
    private String uuid;
    private String name;
    private String contact;
    private Dog dog;

}
