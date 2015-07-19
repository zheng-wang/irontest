package au.com.billon.stt.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;

/**
 * Created by Zheng on 19/07/2015.
 */
@JsonDeserialize(using=AssertionDeserializer.class)
public class Assertion {
    public static final String ASSERTION_TYPE_CONTAINS = "Contains";
    private long id;
    private long teststepId;
    private String name;
    private String type;
    private Properties properties;
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

    public Properties getProperties() throws JsonProcessingException {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
