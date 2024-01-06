package com.temporal.demos.temporalspringbootdemo.activities.clean;

import com.temporal.demos.temporalspringbootdemo.activities.compensate.CompensateActivityImpl;
import io.temporal.spring.boot.ActivityImpl;
import io.temporal.workflow.Workflow;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ActivityImpl(taskQueues = {"CleanTaskQueue"})
public class CleanActivityImpl implements CleanActivity{

    private static final Logger logger = Workflow.getLogger(CleanActivityImpl.class);
    @Override
    public void clean() {
        logger.info("Start Clean Activity");

    }
}
