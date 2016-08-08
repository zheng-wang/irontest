package io.irontest.core.runner;

import io.irontest.models.Teststep;
import io.irontest.models.WaitTeststepProperties;

/**
 * Created by zhenw9 on 10/06/2016.
 */
public class WaitTeststepRunner extends TeststepRunner {
    protected Object run(Teststep teststep) throws InterruptedException {
        WaitTeststepProperties teststepProperties = (WaitTeststepProperties) teststep.getOtherProperties();
        Thread.sleep(teststepProperties.getSeconds() * 1000);
        return null;
    }
}