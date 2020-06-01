package io.irontest.core.teststep;

import io.irontest.models.teststep.Teststep;
import io.irontest.models.teststep.WaitTeststepProperties;

public class WaitTeststepRunner extends TeststepRunner {
    protected BasicTeststepRun run(Teststep teststep) throws InterruptedException {
        WaitTeststepProperties teststepProperties = (WaitTeststepProperties) teststep.getOtherProperties();
        long milliseconds = Long.valueOf(teststepProperties.getMilliseconds());
        if (milliseconds > 0) {
            Thread.sleep(milliseconds);
        }
        return new BasicTeststepRun();
    }
}