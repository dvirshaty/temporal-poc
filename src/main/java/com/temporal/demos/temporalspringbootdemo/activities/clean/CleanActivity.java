package com.temporal.demos.temporalspringbootdemo.activities.clean;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface CleanActivity {

    public void clean();
}
