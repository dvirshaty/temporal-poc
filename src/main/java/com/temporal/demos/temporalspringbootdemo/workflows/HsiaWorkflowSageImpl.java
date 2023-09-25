package com.temporal.demos.temporalspringbootdemo.workflows;

import com.temporal.demos.temporalspringbootdemo.activities.compensate.CompensateActivity;
import com.temporal.demos.temporalspringbootdemo.activities.hsia.HsiaActivity;
import com.temporal.demos.temporalspringbootdemo.activities.ssdf.AtpCallbackActivity;
import com.temporal.demos.temporalspringbootdemo.activities.ssdf.GetAbrActivity;
import com.temporal.demos.temporalspringbootdemo.activities.ssdf.SetAbrActivity;
import com.temporal.demos.temporalspringbootdemo.config.HsiaWorkflowConfig;
import com.temporal.demos.temporalspringbootdemo.dto.HsiaDto;
import com.temporal.demos.temporalspringbootdemo.repository.HisaRepository;
import com.temporal.demos.temporalspringbootdemo.repository.model.Hsia;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.List;

@WorkflowImpl(taskQueues = "HsiaTaskQueue")
@RequiredArgsConstructor
public class HsiaWorkflowSageImpl implements HsiaWorkflowSaga {
    private static final Logger logger = Workflow.getLogger(HsiaWorkflowSageImpl.class);
    private final HisaRepository hisaRepository;
    private final HsiaWorkflowConfig hsiaWorkflowConfig;

    private final AtpCallbackActivity atpCallbackActivity = Workflow.newActivityStub(AtpCallbackActivity.class,
            ActivityOptions.newBuilder().setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                    .setStartToCloseTimeout(Duration.ofSeconds(20)).build());
    private final GetAbrActivity getAbrActivity = Workflow.newActivityStub(GetAbrActivity.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(20)).build());
    private final SetAbrActivity setAbrActivity = Workflow.newActivityStub(SetAbrActivity.class,
                    ActivityOptions.newBuilder().setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(2).build())
                            .setStartToCloseTimeout(Duration.ofSeconds(20)).build());

    private final HsiaActivity hsiaActivity = Workflow.newActivityStub(HsiaActivity.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(20)).build());


    private final CompensateActivity compensateActivity = Workflow.newActivityStub(CompensateActivity.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(20)).build());

    private boolean isAtpCallback = false;
    private boolean isBrassCallback = false;


    @Override // WorkflowMethod
    public void validateAndExecute(HsiaDto input) {
        Saga saga = new Saga(new Saga.Options.Builder().setParallelCompensation(false).build());

        saveToDB(input);
        try {
            logger.info("start workflow !!!");
            saga.addCompensation(compensateActivity::compensate, input);
            setAbrActivity.setAbr(input);
            logger.info("wait for ATP");
            Workflow.await(Duration.ofSeconds(hsiaWorkflowConfig.getWaitForAtpSeconds()), () -> isAtpCallback);
            atpCallbackActivity.handleAtpCallback(isAtpCallback);
            getAbrActivity.getAbr(input);
            hsiaActivity.submitHsia(input);
            saga.addCompensation(compensateActivity::compensate2, input);
            logger.info("wait for BRASS callback");
            Workflow.await(Duration.ofSeconds(hsiaWorkflowConfig.getWaitForHsiaCallbackSeconds()), () -> isBrassCallback);
            hsiaActivity.sspCallback(input);
            logger.info("Done workflow !!!");
        } catch (Exception e) {
            logger.info("workflow failed, try to compensate");
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
