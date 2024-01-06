package com.temporal.demos.temporalspringbootdemo.activities.compensate;

import com.temporal.demos.temporalspringbootdemo.dto.HsiaDto;
import com.temporal.demos.temporalspringbootdemo.dto.MaestroError;
import io.temporal.failure.ApplicationFailure;
import io.temporal.spring.boot.ActivityImpl;
import io.temporal.workflow.Workflow;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ActivityImpl(taskQueues = {"HsiaTaskQueue"})
public class CompensateActivityImpl implements CompensateActivity {
    private static final Logger logger = Workflow.getLogger(CompensateActivityImpl.class);

    @Override
    public void alertException(MaestroError ex) {
        logger.error("fire alert {}  {}", ex.getErrorType(), ex.getErrorMessage());
    }

    @Override
    public void rollbackDB(HsiaDto hsiaDto) {
        logger.info("some rollback logic");
    }


}
