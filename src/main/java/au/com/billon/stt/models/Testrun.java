package au.com.billon.stt.models;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/24/15.
 */
public class Testrun {
    private List<Testcase> testcases;
    private Testcase testcase;
    private List<Teststep> teststeps;
    private Teststep teststep;
    private String request;
    private String response;
    private Environment environment;
    private Endpoint endpoint;
    private Map<String, String> details;
    private Date created;

    public Testrun() {
    }

    public Testrun(List<Testcase> testcases, Testcase testcase, List<Teststep> teststeps, Teststep teststep, String request, String response,
                   Environment environment, Endpoint endpoint, Map<String, String> details, Date created) {
        this.testcases = testcases;
        this.testcase = testcase;
        this.teststeps = teststeps;
        this.teststep = teststep;
        this.request = request;
        this.response = response;
        this.environment = environment;
        this.endpoint = endpoint;
        this.details = details;
        this.created = created;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
