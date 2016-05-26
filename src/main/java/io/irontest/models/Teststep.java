package io.irontest.models;

import java.util.Date;

/**
 * Created by Zheng on 7/07/2015.
 */
public class Teststep {
    public static final String TEST_STEP_TYPE_SOAP = "SOAP";
    public static final String TEST_STEP_TYPE_DB = "DB";
    public static final String TEST_STEP_TYPE_IIB = "IIB";
    private long id;
    private long testcaseId;
    private short sequence;
    private String name;
    private String type;
    private String description;
    private String request;
    private Endpoint endpoint;
    private Date created;
    private Date updated;

    public Teststep() {}

    public Teststep(long id, long testcaseId, short sequence, String name, String type, String description,
                    Date created, Date updated, String request) {
        this.id = id;
        this.testcaseId = testcaseId;
        this.sequence = sequence;
        this.name = name;
        this.type = type;
        this.description = description;
        this.created = created;
        this.updated = updated;
        this.request = request;
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

    public short getSequence() {
        return sequence;
    }

    public void setSequence(short sequence) {
        this.sequence = sequence;
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
}
