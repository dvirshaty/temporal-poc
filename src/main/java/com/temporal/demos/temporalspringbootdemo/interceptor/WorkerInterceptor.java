package com.temporal.demos.temporalspringbootdemo.interceptor;

import io.temporal.common.interceptors.ActivityInboundCallsInterceptor;
import io.temporal.common.interceptors.WorkflowInboundCallsInterceptor;

public class WorkerInterceptor implements io.temporal.common.interceptors.WorkerInterceptor {

    @Override
    public WorkflowInboundCallsInterceptor interceptWorkflow(WorkflowInboundCallsInterceptor next) {
        return null;
    }

    @Override
    public ActivityInboundCallsInterceptor interceptActivity(ActivityInboundCallsInterceptor next) {
        return new PauseInterceptor(next);
    }
}
