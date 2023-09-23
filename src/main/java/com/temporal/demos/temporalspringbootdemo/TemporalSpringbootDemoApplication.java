package com.temporal.demos.temporalspringbootdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class TemporalSpringbootDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemporalSpringbootDemoApplication.class, args).start();
    }
}
