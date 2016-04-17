package io.irontest.models;

import java.util.Date;
import java.util.List;

/**
 * Created by Trevor Li on 7/24/15.
 */
public class Testrun {
    private List<Long> testcaseIds;
    private List<Testcase> testcases;
    private long testcaseId;
    private Testcase testcase;
    private List<Long> teststepIds;
    private List<Teststep> teststeps;
    private long teststepId;
    private Teststep teststep;
    private String request;
    private Object response;
    private long environmentId;
    private Environment environment;
    private Date created;

    public Testrun() {}

    public List<Testcase> getTestcases() {
        return testcases;
    }

    public void setTestcases(List<Testcase> testcases) {
        this.testcases = testcases;
    }

    public Testcase getTestcase() {
        return testcase;
    }

    public void setTestcase(Testcase testcase) {
        this.testcase = testcase;
    }

    public List<Teststep> getTeststeps() {
        return teststeps;
    }

    public void setTeststeps(List<Teststep> teststeps) {
        this.teststeps = teststeps;
    }

    public Teststep getTeststep() {
        return teststep;
    }

    public void setTeststep(Teststep teststep) {
        this.teststep = teststep;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public List<Long> getTestcaseIds() {
        return testcaseIds;
    }

    public void setTestcaseIds(List<Long> testcaseIds) {
        this.testcaseIds = testcaseIds;
    }

    public long getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(long testcaseId) {
        this.testcaseId = testcaseId;
    }

    public List<Long> getTeststepIds() {
        return teststepIds;
    }

    public void setTeststepIds(List<Long> teststepIds) {
        this.teststepIds = teststepIds;
    }

    public long getTeststepId() {
        return teststepId;
    }

    public void setTeststepId(long teststepId) {
        this.teststepId = teststepId;
    }

    public long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(long environmentId) {
        this.environmentId = environmentId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
