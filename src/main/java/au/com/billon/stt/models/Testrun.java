package au.com.billon.stt.models;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private String response;
    private long environmentId;
    private Environment environment;
    private long endpointId;
    private Endpoint endpoint;
    private Map<String, String> details;
    private Date created;

    public Testrun() {
    }

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

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
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

    public long getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(long endpointId) {
        this.endpointId = endpointId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
