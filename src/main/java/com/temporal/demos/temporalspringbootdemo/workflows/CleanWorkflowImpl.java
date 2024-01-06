package com.temporal.demos.temporalspringbootdemo.workflows;

import com.temporal.demos.temporalspringbootdemo.activities.clean.CleanActivity;
import com.temporal.demos.temporalspringbootdemo.activities.ssdf.GetAbrActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

@WorkflowImpl(taskQueues = "CleanTaskQueue")
public class CleanWorkflowImpl implements CleanWorkflow {
    private static final Logger log = Workflow.getLogger(CleanWorkflowImpl.class);


    private final CleanActivity cleanActivity = Workflow.newActivityStub(CleanActivity.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(20)).build());
    private final GetAbrActivity getAbrActivity = Workflow.newActivityStub(GetAbrActivity.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(20)).build());
    @Override
    public void clean() {
        log.info("Start CleanWorkflow - {}", Workflow.getInfo().getWorkflowId());


        cleanActivity.clean();
        getAbrActivity.clean();

        log.info("dONE CleanWorkflow");
    }
}
