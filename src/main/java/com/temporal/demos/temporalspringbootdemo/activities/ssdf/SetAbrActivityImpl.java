package com.temporal.demos.temporalspringbootdemo.activities.ssdf;

import com.temporal.demos.temporalspringbootdemo.dto.HsiaDto;
import com.temporal.demos.temporalspringbootdemo.exception.SsdfException;
import io.temporal.spring.boot.ActivityImpl;
import io.temporal.workflow.Workflow;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ActivityImpl(taskQueues = {"HsiaTaskQueue"})
public class SetAbrActivityImpl implements SetAbrActivity {
    private static final Logger logger = Workflow.getLogger(SetAbrActivityImpl.class);

    @Override
    public void setAbr(HsiaDto input) {

        logger.info(MDC.getCopyOfContextMap().toString());

        MDC.put("tid", input.getUuid());
        try {


            logger.info("set ABR to SSDF - {}", input);
            throw new SsdfException("dd");
            //    throw new NonRetryException("Failed setABR");
     /*    Random rd = new Random(); // creating Random boolean
         if (!rd.nextBoolean()) {
            //throw new RuntimeException("Failed setABR");
         //   throw new NonRetryException("Failed setABR");
          throw ApplicationFailure.newFailure(
                    "Product "
                            + input.getId()
                            + " is not in stock for amount "
                            + input.getName(),
                    RuntimeException.class.getName(),
                    input);
        }*/

        } finally {
            MDC.clear();
        }
    }

}
