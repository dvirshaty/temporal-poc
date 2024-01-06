package com.temporal.demos.temporalspringbootdemo.workflows;

import com.temporal.demos.temporalspringbootdemo.activities.compensate.CompensateActivity;
import com.temporal.demos.temporalspringbootdemo.activities.hsia.HsiaActivity;
import com.temporal.demos.temporalspringbootdemo.activities.ssdf.AtpCallbackActivity;
import com.temporal.demos.temporalspringbootdemo.activities.ssdf.GetAbrActivity;
import com.temporal.demos.temporalspringbootdemo.activities.ssdf.SetAbrActivity;
import com.temporal.demos.temporalspringbootdemo.config.HsiaWorkflowConfig;
import com.temporal.demos.temporalspringbootdemo.dto.HsiaDto;
import com.temporal.demos.temporalspringbootdemo.dto.MaestroError;
import com.temporal.demos.temporalspringbootdemo.dto.MaestroErrorType;
import com.temporal.demos.temporalspringbootdemo.exception.NonRetryException;
import com.temporal.demos.temporalspringbootdemo.repository.HisaRepository;
import com.temporal.demos.temporalspringbootdemo.repository.model.Hsia;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@WorkflowImpl(taskQueues = "HsiaTaskQueue")
@RequiredArgsConstructor
public class HsiaWorkflowSageImpl implements HsiaWorkflowSaga {
    private static final Logger logger = Workflow.getLogger(HsiaWorkflowSageImpl.class);
    private final HisaRepository hisaRepository;
    private final HsiaWorkflowConfig hsiaWorkflowConfig;
    private List<String> errorList;
    private final AtpCallbackActivity atpCallbackActivity = Workflow.newActivityStub(AtpCallbackActivity.class,
            ActivityOptions.newBuilder().setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build()).setTaskQueue("HsiaTaskQueue")
                    .setStartToCloseTimeout(Duration.ofSeconds(20)).build());
    private final GetAbrActivity getAbrActivity = Workflow.newActivityStub(GetAbrActivity.class,
            ActivityOptions.newBuilder().setTaskQueue("HsiaTaskQueue").setStartToCloseTimeout(Duration.ofSeconds(20)).build());
    private SetAbrActivity setAbrActivity;

    private final HsiaActivity hsiaActivity = Workflow.newActivityStub(HsiaActivity.class,
            ActivityOptions.newBuilder().setTaskQueue("CleanTaskQueue").setStartToCloseTimeout(Duration.ofSeconds(20)).build());


    private final CompensateActivity compensateActivity = Workflow.newActivityStub(CompensateActivity.class,
            ActivityOptions.newBuilder().setTaskQueue("HsiaTaskQueue").setStartToCloseTimeout(Duration.ofSeconds(20)).build());

    private boolean isAtpCallback = false;
    private boolean isBrassCallback = false;


    public void init(){

        setAbrActivity = Workflow.newActivityStub(SetAbrActivity.class,
                ActivityOptions.newBuilder().setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(2).setDoNotRetry(buildListOfErrors()).build()).setTaskQueue("HsiaTaskQueue")
                        .setStartToCloseTimeout(Duration.ofSeconds(20)).build());
    }

    private String[] buildListOfErrors() {
        Reflections reflections = new Reflections("com.temporal.demos.temporalspringbootdemo.exception");
        ArrayList<String> strings = new ArrayList<>();
        Set<Class<? extends NonRetryException>> subTypesOf = reflections.getSubTypesOf(NonRetryException.class);
        for (Class<? extends NonRetryException> aClass : subTypesOf) {
                strings.add(aClass.getName());
        }
        String[] result = strings.toArray(String[]::new);
        return result;
    }

    @Override
    public void validateAndExecute(HsiaDto input) {
        init();
        Saga saga = new Saga(new Saga.Options.Builder().setParallelCompensation(false).build());
        MaestroError error = new MaestroError();


        try {


            saga.addCompensation(compensateActivity::alertException, error);
            logger.info("start workflow !!!");
            setAbrActivity.setAbr(input);
            logger.info("wait for ATP");
            
/*            Workflow.await(Duration.ofSeconds(hsiaWorkflowConfig.getWaitForAtpSeconds()), () -> isAtpCallback);
            atpCallbackActivity.handleAtpCallback(isAtpCallback);
            getAbrActivity.getAbr(input);*/
            hsiaActivity.submitHsia(input);
            saga.addCompensation(compensateActivity::rollbackDB, input);
            logger.info("wait for BRASS callback");
        //    Workflow.await(Duration.ofSeconds(hsiaWorkflowConfig.getWaitForHsiaCallbackSeconds()), () -> isBrassCallback);
            hsiaActivity.sspCallback(input);
            logger.info("Done workflow !!!");
        } catch (Exception e) {
            error.setErrorType(MaestroErrorType.ERROR_3);
            error.setErrorMessage(e.getMessage());
            saga.compensate();
        }
    }

    private void saveToDB(HsiaDto input) {
        Hsia hsia = Hsia.builder().uuid(input.getUuid()).name(input.getName()).contact(input.getContact()).build();
        Hsia save = hisaRepository.save(hsia);
        input.setId(save.getId());
    }

    @Override
    public void setAtp(String uuid) {
        logger.info("got ATP!");
        List<Hsia> byUuid = hisaRepository.findByUuid(uuid);
        if (!CollectionUtils.isEmpty(byUuid)) {
            isAtpCallback = true;
        }
    }

    @Override
    public void setBrassCallback() {
        logger.info("got Brass callback !");
        isBrassCallback = true;
    }


}
