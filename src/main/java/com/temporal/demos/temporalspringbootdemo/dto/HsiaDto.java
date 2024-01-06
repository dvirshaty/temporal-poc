package com.temporal.demos.temporalspringbootdemo.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.io.Serializable;

@Data
public class HsiaDto implements Serializable {

    private Long id;
    private String uuid;
    private String name;
    private String contact;
    private String contact2;

    private JsonNode data;
    private Person person;
    private Address address;

}
