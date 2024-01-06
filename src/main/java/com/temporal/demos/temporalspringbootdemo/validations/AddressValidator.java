package com.temporal.demos.temporalspringbootdemo.validations;

import com.temporal.demos.temporalspringbootdemo.dto.Address;
import com.temporal.demos.temporalspringbootdemo.dto.HsiaDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressValidator implements PrivateValidation {
    @Override
    public List<String> validate(HsiaDto hsiaDto, List<String> errors) {
        Address address = hsiaDto.getAddress();
        if (address == null) {
            errors.add("address is null");
        }
        return errors;
    }
}
