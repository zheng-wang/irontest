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

    public Object run() throws Exception {
        prepareTeststep(teststep, teststepDAO);
        return run(teststep);
    }

    /**
     * Sub class can optionally override.
     * @param teststep
     * @param teststepDAO
     */
    protected void prepareTeststep(Teststep teststep, TeststepDAO teststepDAO) {
        //  decrypt password in endpoint
        Endpoint endpoint = teststep.getEndpoint();
        if (endpoint != null && endpoint.getPassword() != null) {
            endpoint.setPassword(utilsDAO.decryptPassword(endpoint.getPassword()));
        }
    }

    protected abstract Object run(Teststep teststep) throws Exception;

    protected void setTeststep(Teststep teststep) {
        this.teststep = teststep;
    }

    protected void setTeststepDAO(TeststepDAO teststepDAO) {
        this.teststepDAO = teststepDAO;
    }

    protected void setUtilsDAO(UtilsDAO utilsDAO) {
        this.utilsDAO = utilsDAO;
    }
}
