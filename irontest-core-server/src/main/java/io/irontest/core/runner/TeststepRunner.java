package io.irontest.core.runner;

import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.Teststep;

/**
 * Created by Trevor Li on 7/14/15.
 */
public abstract class TeststepRunner {
    private Teststep teststep;
    private TeststepDAO teststepDAO;
    private UtilsDAO utilsDAO;
    private TestcaseRunContext testcaseRunContext;

    protected TeststepRunner() {}

    public BasicTeststepRun run() throws Exception {
        prepareTeststep();
        return run(teststep);
    }

    /**
     * Sub class can optionally override.
     */
    protected void prepareTeststep() {
        //  decrypt password in endpoint
        Endpoint endpoint = this.teststep.getEndpoint();
        if (endpoint != null && endpoint.getPassword() != null) {
            endpoint.setPassword(this.utilsDAO.decryptPassword(endpoint.getPassword()));
        }
    }

    protected abstract BasicTeststepRun run(Teststep teststep) throws Exception;

    protected void setTeststep(Teststep teststep) {
        this.teststep = teststep;
    }

    protected void setTeststepDAO(TeststepDAO teststepDAO) {
        this.teststepDAO = teststepDAO;
    }

    protected Teststep getTeststep() {
        return teststep;
    }

    protected TeststepDAO getTeststepDAO() {
        return teststepDAO;
    }

    protected void setUtilsDAO(UtilsDAO utilsDAO) {
        this.utilsDAO = utilsDAO;
    }

    protected TestcaseRunContext getTestcaseRunContext() {
        return testcaseRunContext;
    }

    protected void setTestcaseRunContext(TestcaseRunContext testcaseRunContext) {
        this.testcaseRunContext = testcaseRunContext;
    }
}
