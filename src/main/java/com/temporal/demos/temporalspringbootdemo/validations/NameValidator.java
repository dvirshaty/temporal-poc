package com.temporal.demos.temporalspringbootdemo.validations;

import com.temporal.demos.temporalspringbootdemo.dto.HsiaDto;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class NameValidator implements PrivateValidation{


    @Override
    public List<String> validate(HsiaDto hsiaDto, List<String> errors) {

        if( hsiaDto.getName() ==null || hsiaDto.getName().isEmpty()){
            errors.add("name is empty");
        }


        if( hsiaDto.getUuid() ==null || hsiaDto.getUuid().isEmpty()){
            errors.add("uuid is empty");
        }


        if( hsiaDto.getContact() ==null || hsiaDto.getContact().isEmpty()){
            errors.add("Contact is empty");
        }

        if( hsiaDto.getContact2() ==null || hsiaDto.getContact2().isEmpty()){
            errors.add("Contact2 is empty");
        }

        return errors;
    }
}
