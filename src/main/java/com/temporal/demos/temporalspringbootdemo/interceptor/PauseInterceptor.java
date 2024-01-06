package com.temporal.demos.temporalspringbootdemo.interceptor;

import io.temporal.activity.ActivityExecutionContext;
import io.temporal.common.interceptors.ActivityInboundCallsInterceptor;
import io.temporal.common.interceptors.ActivityInboundCallsInterceptorBase;
import io.temporal.common.interceptors.WorkflowInboundCallsInterceptor;
import io.temporal.common.interceptors.WorkflowInboundCallsInterceptorBase;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;


@Slf4j
public class PauseInterceptor extends ActivityInboundCallsInterceptorBase {

    private ActivityExecutionContext activityExecutionContext;
    private final ActivityInboundCallsInterceptor next;
    public PauseInterceptor(ActivityInboundCallsInterceptor next) {
        super(next);
        this.next = next;
    }

    public ActivityInboundCallsInterceptor.ActivityOutput execute(ActivityInboundCallsInterceptor.ActivityInput input) {

        log.info("Enter ActivityInboundCallsInterceptor");
        Random random = new Random();

        int i = 1;

        while (i < 3) {
            if (random.nextBoolean()) {
                i++;
            }
            Workflow.sleep(1000);
        }


        return this.next.execute(input);
    }
/*    @Override
    public void init(ActivityExecutionContext context) {

    }

    @Override
    public ActivityOutput execute(ActivityInput input) {
        return null;
    }*/
}
 /*   @Override
    public ActivityOutput execute(ActivityInput input) {
        log.info("Enter ActivityInboundCallsInterceptor");
        Random random = new Random();

        int i = 1;

        while (i < 3) {
            if (random.nextBoolean()) {
                i++;
            }
            Workflow.sleep(1000);
        }

        return null;
    }
}*/
