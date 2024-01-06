package com.temporal.demos.temporalspringbootdemo.exception;

public class SsdfException extends NonRetryException {

    public SsdfException(String message) {
        super(message);
    }
}
