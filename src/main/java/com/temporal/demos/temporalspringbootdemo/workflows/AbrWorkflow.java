package com.temporal.demos.temporalspringbootdemo.workflows;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;

@WorkflowInterface
public interface AbrWorkflow {

    @SignalMethod
    void setAtp();
}
