package com.temporal.demos.temporalspringbootdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.temporal.demos.temporalspringbootdemo.dto.*;
import com.temporal.demos.temporalspringbootdemo.repository.HisaRepository;
import com.temporal.demos.temporalspringbootdemo.repository.model.Hsia;
import com.temporal.demos.temporalspringbootdemo.validations.HsiaDtoValidator;
import com.temporal.demos.temporalspringbootdemo.workflows.CleanWorkflow;
import com.temporal.demos.temporalspringbootdemo.workflows.HsiaWorkflowSaga;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import io.temporal.worker.WorkerFactoryOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Controller
@RequestMapping("/hsia")
@RequiredArgsConstructor
public class SamplesController {

    private final WorkflowClient client;
    private final HisaRepository hisaRepository;
    private final HsiaDtoValidator hsiaDtoValidator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        log.info("init");
        CleanWorkflow workflow1 =
                client.newWorkflowStub(
                        CleanWorkflow.class,
                        WorkflowOptions.newBuilder()
                                .setWorkflowId("cleanWF")
                                .setTaskQueue("CleanTaskQueue")

                                .build());


    }


/*    @PostMapping("/submit")
    ResponseEntity<Boolean> hsia(@RequestBody HsiaDto hsiaDto) {
        log.info("validate Hsia payload");
        validate();
        log.info("done validate Hsia payload");

        String uuid = UUID.randomUUID().toString();
        log.info("workflow uuid - {}", uuid);
        HsiaWorkflow workflow = client.newWorkflowStub(HsiaWorkflow.class, WorkflowOptions.newBuilder().setTaskQueue("HsiaTaskQueue").setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build()).setWorkflowId(uuid).build());

        WorkflowClient.start(workflow::validateAndExecute, hsiaDto);
        // workflow.validateAndExecute(input);
        log.info("done web controller");
        return ResponseEntity.ok(true);
    }*/

    @PostMapping("/submit/saga")
    ResponseEntity<Boolean> hsiaSaga(@RequestBody HsiaDto hsiaDto) {
        log.info("validate Hsia With Saga pattern payload");
        List<String> validate = validate(hsiaDto);
   /*     if (!validate.isEmpty()) {
            log.info(validate.toString());
            return ResponseEntity.ok(false);
        }*/


        String uuid = UUID.randomUUID().toString();
        hsiaDto.setUuid(uuid);

        log.info("done validate Hsia payload");




        log.info("workflow uuid - {}", uuid);
        HsiaWorkflowSaga workflow = client.newWorkflowStub(HsiaWorkflowSaga.class,
                WorkflowOptions.newBuilder().setTaskQueue("HsiaTaskQueue").setRetryOptions(RetryOptions.newBuilder()
                        .setMaximumAttempts(1).build()).setWorkflowId(uuid).build());
         //WorkflowClient.start(workflow::validateAndExecute, hsiaDto);
        WorkflowClient.execute(workflow::validateAndExecute, hsiaDto);
        // workflow.validateAndExecute(input);
        log.info("done web controller");
        return ResponseEntity.ok(true);
    }

    private List<String> validate(HsiaDto hsiaDto) {
        return hsiaDtoValidator.validate(hsiaDto);
    }

    @PostMapping("/save/db")
    Long saveToDB(@RequestBody HsiaDto hsiaDto) {
        log.info("saveToDB");
        Hsia hsia = Hsia.builder().data(hsiaDto.getData()).name(hsiaDto.getName()).contact(hsiaDto.getContact()).build();
        Hsia save = hisaRepository.save(hsia);
        return save.getId();
    }

    @GetMapping("/getDog/db/{id}")
    ResponseEntity<HsiaDtoDog> getDogFromDB(@PathVariable Long id) {
        log.info("get from");
        HsiaDtoDog dog = new HsiaDtoDog();
        Optional<Hsia> byId = hisaRepository.findById(id);
        Hsia hsia = byId.get();
        dog.setName(hsia.getName());
        dog.setId(hsia.getId());
        dog.setContact(hsia.getContact());
        dog.setUuid(hsia.getUuid());
        dog.setDog(objectMapper.convertValue(hsia.getData(), Dog.class));
        log.info("dog - {}", dog);
        return ResponseEntity.ok(dog);
    }

    @GetMapping("/getCat/db/{id}")
    ResponseEntity<HsiaDtoCat> getCatFromDB(@PathVariable Long id) {
        log.info("get from");
        HsiaDtoCat cat = new HsiaDtoCat();
        Optional<Hsia> byId = hisaRepository.findById(id);
        Hsia hsia = byId.get();
        cat.setName(hsia.getName());
        cat.setId(hsia.getId());
        cat.setContact(hsia.getContact());
        cat.setUuid(hsia.getUuid());
        cat.setCat(objectMapper.convertValue(hsia.getData(), Cat.class));
        log.info("cat - {}", cat);
        return ResponseEntity.ok(cat);
    }


    @GetMapping("/setAtp/{uuid}")
    ResponseEntity<Boolean> setATPSignal(@PathVariable String uuid) {
        log.info("workflow setATPSignal - {}", uuid);
        HsiaWorkflowSaga workflow = client.newWorkflowStub(HsiaWorkflowSaga.class, uuid);
        workflow.setAtp(uuid);
        return ResponseEntity.ok(true);

    }


    public static void main(String[] args) {
        EmailValidator instance = EmailValidator.getInstance();

        System.out.printf(" " + instance.isValid(null));
    }
}


