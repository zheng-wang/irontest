package io.irontest.core.teststep;

import io.irontest.models.teststep.WaitTeststepProperties;

public class WaitTeststepRunner extends TeststepRunner {
    public BasicTeststepRun run() throws InterruptedException {
        WaitTeststepProperties teststepProperties = (WaitTeststepProperties) getTeststep().getOtherProperties();
        long milliseconds = Long.valueOf(teststepProperties.getMilliseconds());
        if (milliseconds > 0) {
            Thread.sleep(milliseconds);
        }
        return new BasicTeststepRun();
    }
}