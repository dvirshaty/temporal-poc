package com.temporal.demos.temporalspringbootdemo.dto;

import lombok.Data;

@Data
public class MaestroError {

    private String errorMessage;
    private MaestroErrorType errorType;
}
