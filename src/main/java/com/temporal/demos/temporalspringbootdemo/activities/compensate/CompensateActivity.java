package com.temporal.demos.temporalspringbootdemo.activities.compensate;

import com.temporal.demos.temporalspringbootdemo.dto.HsiaDto;
import com.temporal.demos.temporalspringbootdemo.dto.MaestroError;
import io.temporal.activity.ActivityInterface;
import io.temporal.failure.ApplicationFailure;

@ActivityInterface
public interface CompensateActivity {

    public void alertException(MaestroError Err);

    public void rollbackDB(HsiaDto hsiaDto);
}
