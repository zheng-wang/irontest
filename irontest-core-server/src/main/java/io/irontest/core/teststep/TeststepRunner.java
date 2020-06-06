package io.irontest.core.teststep;

import io.irontest.core.testcase.TestcaseRunContext;
import io.irontest.models.teststep.Teststep;

public abstract class TeststepRunner {
    private Teststep teststep;
    private String decryptedEndpointPassword;
    private TestcaseRunContext testcaseRunContext;    //  set only when running test case

    protected TeststepRunner() {}

    public abstract BasicTeststepRun run() throws Exception;

    public void setDecryptedEndpointPassword(String decryptedEndpointPassword) {
        this.decryptedEndpointPassword = decryptedEndpointPassword;
    }

    protected void setTeststep(Teststep teststep) {
        this.teststep = teststep;
    }

    protected Teststep getTeststep() {
        return teststep;
    }

    protected String getDecryptedEndpointPassword() {
        return decryptedEndpointPassword;
    }

    protected TestcaseRunContext getTestcaseRunContext() {
        return testcaseRunContext;
    }

    void setTestcaseRunContext(TestcaseRunContext testcaseRunContext) {
        this.testcaseRunContext = testcaseRunContext;
    }
}
