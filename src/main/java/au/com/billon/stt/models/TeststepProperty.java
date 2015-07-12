package au.com.billon.stt.models;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Zheng on 7/07/2015.
 */
public class TeststepProperty {
    public static final String PROPERTY_NAME_SOAP_ADDRESS = "soapAddress";

    private long id;
    private long teststepId;
    private String name;
    private String value;
    private Date created;
    private Date updated;

    public TeststepProperty() {}

    public TeststepProperty(long teststepId, String name, String value) {
        this.teststepId = teststepId;
        this.name = name;
        this.value = value;
    }

    public TeststepProperty(long id, long teststepId, String name, String value, Date created, Date updated) {
        this.id = id;
        this.teststepId = teststepId;
        this.name = name;
        this.value = value;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
}
