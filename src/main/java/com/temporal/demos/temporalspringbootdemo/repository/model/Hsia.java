package com.temporal.demos.temporalspringbootdemo.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
public class Hsia {
    @Id
    @GeneratedValue
    private Long id;
    private String uuid;
    private String name;
    private String contact;
}
