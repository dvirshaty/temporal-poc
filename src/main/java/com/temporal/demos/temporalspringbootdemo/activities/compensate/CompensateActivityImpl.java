package com.temporal.demos.temporalspringbootdemo.activities.compensate;

import com.temporal.demos.temporalspringbootdemo.dto.HsiaDto;
import io.temporal.spring.boot.ActivityImpl;
import io.temporal.workflow.Workflow;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ActivityImpl(taskQueues = "HsiaTaskQueue")
public class CompensateActivityImpl implements CompensateActivity {

    private static final Logger logger = Workflow.getLogger(CompensateActivityImpl.class);

    @Override
    public void compensate(HsiaDto hsiaDto) {
        logger.error("Fire Alert workflow failed for {}", hsiaDto);

    }

    @Override
    public void compensate2(HsiaDto hsiaDto) {
        logger.error("Fire Alert2 workflow failed for {}", hsiaDto);
    }


}
