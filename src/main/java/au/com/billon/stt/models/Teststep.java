package au.com.billon.stt.models;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by Zheng on 7/07/2015.
 */
public class Teststep {
    private long id;
    private long testcaseId;
    private String name;
    private String description;
    private String wsdlUrl;
    private String wsdlBindingName;
    private String wsdlOperationName;
    private String request;
    private Date created;
    private Date updated;
    private List<TeststepProperty> properties;

    public Teststep() {}

    public Teststep(long id, long testcaseId, String name, String description, Date created, Date updated, String request) {
        this.id = id;
        this.testcaseId = testcaseId;
        this.name = name;
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

    public List<TeststepProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<TeststepProperty> properties) {
        this.properties = properties;
    }

    public String getWsdlUrl() {
        return wsdlUrl;
    }

    public void setWsdlUrl(String wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
    }

    public String getWsdlBindingName() {
        return wsdlBindingName;
    }

    public void setWsdlBindingName(String wsdlBindingName) {
        this.wsdlBindingName = wsdlBindingName;
    }

    public String getWsdlOperationName() {
        return wsdlOperationName;
    }

    public void setWsdlOperationName(String wsdlOperationName) {
        this.wsdlOperationName = wsdlOperationName;
    }
}
