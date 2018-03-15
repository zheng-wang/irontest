package io.irontest.core.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.db.*;
import io.irontest.models.testrun.TestcaseRun;

/**
 * Created by Zheng on 15/03/2018.
 */
public abstract class TestcaseRunner {
    private long testcaseId;
    private TestcaseDAO testcaseDAO;
    private UserDefinedPropertyDAO udpDAO;
    private TeststepDAO teststepDAO;
    private UtilsDAO utilsDAO;
    private TestcaseRunDAO testcaseRunDAO;

    protected TestcaseRunner(long testcaseId, TestcaseDAO testcaseDAO, UserDefinedPropertyDAO udpDAO, TeststepDAO teststepDAO, UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO) {
        this.testcaseId = testcaseId;
        this.testcaseDAO = testcaseDAO;
        this.udpDAO = udpDAO;
        this.teststepDAO = teststepDAO;
        this.utilsDAO = utilsDAO;
        this.testcaseRunDAO = testcaseRunDAO;
    }

    public long getTestcaseId() {
        return testcaseId;
    }

    protected TestcaseDAO getTestcaseDAO() {
        return testcaseDAO;
    }

    protected UserDefinedPropertyDAO getUdpDAO() {
        return udpDAO;
    }

    protected TeststepDAO getTeststepDAO() {
        return teststepDAO;
    }

    protected UtilsDAO getUtilsDAO() {
        return utilsDAO;
    }

    protected TestcaseRunDAO getTestcaseRunDAO() {
        return testcaseRunDAO;
    }

    public abstract TestcaseRun run() throws JsonProcessingException;
}
