package io.irontest.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;

/**
 * Created by Zheng on 19/07/2015.
 */
public class Assertion {
    public static final String ASSERTION_TYPE_CONTAINS = "Contains";
    public static final String ASSERTION_TYPE_XPATH = "XPath";
    public static final String ASSERTION_TYPE_DSFIELD = "DSField";
    private long id;
    private long teststepId;
    private String name;
    private String type;
    private Properties properties;
    private String result;
    private AssertionVerification verification;
    private Date created;
    private Date updated;

    public Assertion() {}

    public Assertion(long id, long teststepId, String name, String type, Properties properties, Date created, Date updated) {
        this.id = id;
        this.teststepId = teststepId;
        this.name = name;
        this.type = type;
        this.properties = properties;
        this.created = created;
        this.updated = updated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTeststepId() {
        return teststepId;
    }

    public void setTeststepId(long teststepId) {
        this.teststepId = teststepId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public Properties getProperties() {
        return properties;
    }

    @JsonDeserialize(using=AssertionPropertiesDeserializer.class)
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public AssertionVerification getVerification() {
        return verification;
    }

    public void setVerification(AssertionVerification verification) {
        this.verification = verification;
    }
}
