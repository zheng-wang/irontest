package io.irontest.core.runner;

import io.irontest.models.teststep.Teststep;

public class HTTPStubRequestsVerificationTeststepRunner extends TeststepRunner {
    @Override
    protected BasicTeststepRun run(Teststep teststep) {
        return new BasicTeststepRun();
    }
}
