package com.temporal.demos.temporalspringbootdemo.validations;


import com.temporal.demos.temporalspringbootdemo.dto.HsiaDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HsiaDtoValidator {
    private final NameValidator privateValidation;
    private final AddressValidator addressValidator;
    private List<PrivateValidation> validations;

    @PostConstruct
    public void init() {
        validations = new ArrayList<>();
        validations.add(addressValidator);
        validations.add(privateValidation);
    }

    public List<String> validate(HsiaDto hsiaDto) {
        List<String> errors = new ArrayList<>();
        validations.parallelStream().forEach(privateValidation1 -> privateValidation1.validate(hsiaDto, errors));
        return errors;
    }


}
