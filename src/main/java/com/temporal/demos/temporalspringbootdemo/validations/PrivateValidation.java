package com.temporal.demos.temporalspringbootdemo.validations;

import com.temporal.demos.temporalspringbootdemo.dto.HsiaDto;

import java.util.List;

public interface PrivateValidation{
    List<String> validate(HsiaDto hsiaDto , List<String> errors );
}
