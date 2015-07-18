package au.com.billon.stt.models;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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
    private long intfaceId;
    private Intface intface;
    private Date created;
    private Date updated;

    public Teststep() {}

    public Teststep(String type) {
        this.type = type;
    }

    public Teststep(long id, long testcaseId, String name, String type, String description, Date created, Date updated, String request, long intfaceId) {
        this.id = id;
        this.testcaseId = testcaseId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.created = created;
        this.updated = updated;
        this.request = request;
        this.intfaceId = intfaceId;
        this.intface = intface;
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

    public long getIntfaceId() {
        return intfaceId;
    }

    public void setIntfaceId(long intfaceId) {
        this.intfaceId = intfaceId;
    }

    public Intface getIntface() {
        return intface;
    }

    public void setIntface(Intface intface) { this.intface = intface; }

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
