package io.irontest.core.runner;

import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.Endpoint;
import io.irontest.models.Teststep;

/**
 * Created by Trevor Li on 7/14/15.
 */
public abstract class TeststepRunner {
    private Teststep teststep;
    private TeststepDAO teststepDAO;
    private UtilsDAO utilsDAO;

    protected TeststepRunner() {}

    protected TeststepRunner(Teststep teststep, TeststepDAO teststepDAO, UtilsDAO utilsDAO) {
        this.teststep = teststep;
        this.teststepDAO = teststepDAO;
        this.utilsDAO = utilsDAO;
    }

    public Object run() throws Exception {
        //  decrypt password in endpoint
        Endpoint endpoint = teststep.getEndpoint();
        if (endpoint != null && endpoint.getPassword() != null) {
            endpoint.setPassword(utilsDAO.decryptPassword(endpoint.getPassword()));
        }

        preRun(teststep, teststepDAO);
        return run(teststep);
    }

    protected void preRun(Teststep teststep, TeststepDAO teststepDAO) {
        //  do nothing here
        //  sub class can optionally override
    }

    protected abstract Object run(Teststep teststep) throws Exception;
}
