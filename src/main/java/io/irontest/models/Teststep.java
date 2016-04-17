package io.irontest.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.irontest.models.assertion.EvaluationResult;

import java.util.Date;

/**
 * Created by Zheng on 7/07/2015.
 */
public class Teststep {
    public static final String TEST_STEP_TYPE_SOAP = "SOAP";
    private long id;
    private long testcaseId;
    private String name;
    private String type;
    private String description;
    private String request;
    private long endpointId;
    private Endpoint endpoint;
    private Properties properties;
    private EvaluationResult result;
    private Date created;
    private Date updated;

    public Teststep() {}

    public Teststep(long id, long testcaseId, String name, String type, String description, Properties properties,
                    Date created, Date updated, String request, long endpointId) {
        this.id = id;
        this.testcaseId = testcaseId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.properties = properties;
        this.created = created;
        this.updated = updated;
        this.request = request;
        this.endpointId = endpointId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(long testcaseId) {
        this.testcaseId = testcaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public long getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(long endpointId) {
        this.endpointId = endpointId;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Properties getProperties() {
        return properties;
    }

    public EvaluationResult getResult() {
        return result;
    }

    public void setResult(EvaluationResult result) {
        this.result = result;
    }

    @JsonDeserialize(using=TeststepPropertiesDeserializer.class)
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
