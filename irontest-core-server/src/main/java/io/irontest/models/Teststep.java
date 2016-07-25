package io.irontest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.assertion.Assertion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Zheng on 7/07/2015.
 */
public class Teststep {
    public static final String TYPE_SOAP = "SOAP";
    public static final String TYPE_DB = "DB";
    public static final String TYPE_IIB = "IIB";
    public static final String TYPE_MQ = "MQ";
    public static final String TYPE_WAIT = "Wait";

    public static final String ACTION_START = "Start";
    public static final String ACTION_STOP = "Stop";
    public static final String ACTION_CLEAR = "Clear";
    public static final String ACTION_CHECK_DEPTH = "CheckDepth";
    public static final String ACTION_DEQUEUE = "Dequeue";
    public static final String ACTION_ENQUEUE = "Enqueue";

    private long id;
    private long testcaseId;
    private short sequence;
    @JsonView(JsonViews.TestcaseRun.class)
    private String name;
    @JsonView(JsonViews.TestcaseRun.class)
    private String type;
    @JsonView(JsonViews.TestcaseRun.class)
    private String description;
    @JsonView(JsonViews.TestcaseRun.class)
    private String action;            //  currently only used in MQ test step and IIB test step
    @JsonView(JsonViews.TestcaseRun.class)
    private Endpoint endpoint;
    @JsonView(JsonViews.TestcaseRun.class)
    private Object request;
    private List<Assertion> assertions = new ArrayList<Assertion>();
    private Date created;
    private Date updated;
    @JsonView(JsonViews.TestcaseRun.class)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type", visible = true, defaultImpl = Properties.class)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = IIBTeststepProperties.class, name = Teststep.TYPE_IIB),
            @JsonSubTypes.Type(value = MQTeststepProperties.class, name = Teststep.TYPE_MQ),
            @JsonSubTypes.Type(value = WaitTeststepProperties.class, name = Teststep.TYPE_WAIT)})
    private Properties otherProperties;

    public Teststep() {}

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

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
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

    public Properties getOtherProperties() {
        return otherProperties;
    }

    public void setOtherProperties(Properties otherProperties) {
        this.otherProperties = otherProperties;
    }

    public List<Assertion> getAssertions() {
        return assertions;
    }

    public void setAssertions(List<Assertion> assertions) {
        this.assertions = assertions;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @JsonIgnore
    public boolean isRequestBinary() {
        boolean result = false;
        if (otherProperties instanceof MQTeststepProperties) {
            MQTeststepProperties properties = (MQTeststepProperties) otherProperties;
            result = ACTION_ENQUEUE.equals(action) &&
                    MQTeststepProperties.ENQUEUE_MESSAGE_FROM_FILE.equals(properties.getEnqueueMessageFrom());
        }
        return result;
    }
}
