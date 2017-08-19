package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.irontest.models.Properties;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.endpoint.Endpoint;

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

    /* of IIB test step */
    public static final String ACTION_START = "Start";
    public static final String ACTION_STOP = "Stop";
    public static final String ACTION_WAIT_FOR_PROCESSING_COMPLETION = "WaitForProcessingCompletion";

    /* of MQ test step */
    public static final String ACTION_CLEAR = "Clear";
    public static final String ACTION_CHECK_DEPTH = "CheckDepth";
    public static final String ACTION_DEQUEUE = "Dequeue";
    public static final String ACTION_ENQUEUE = "Enqueue";
    public static final String ACTION_PUBLISH = "Publish";

    private long id;   //  id being 0 means this is dynamically created test step object (no record in the Teststep database table).
    private long testcaseId;
    private short sequence;
    private String name;
    private String type;
    private String description;
    private String action;            //  currently only used in MQ test step and IIB test step
    private Endpoint endpoint;
    private Object request;
    private List<Assertion> assertions = new ArrayList<Assertion>();
    private Date updated;
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type", visible = true, defaultImpl = Properties.class)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = SOAPTeststepProperties.class, name = Teststep.TYPE_SOAP),
            @JsonSubTypes.Type(value = IIBTeststepProperties.class, name = Teststep.TYPE_IIB),
            @JsonSubTypes.Type(value = MQTeststepProperties.class, name = Teststep.TYPE_MQ),
            @JsonSubTypes.Type(value = WaitTeststepProperties.class, name = Teststep.TYPE_WAIT)})
    private Properties otherProperties = new Properties();

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
            result = (ACTION_ENQUEUE.equals(action) || ACTION_PUBLISH.equals(action)) &&
                    MQMessageFrom.FILE == properties.getMessageFrom();
        }
        return result;
    }
}
