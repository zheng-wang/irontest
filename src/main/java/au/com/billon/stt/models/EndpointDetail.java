package au.com.billon.stt.models;

import java.util.Date;

/**
 * Created by Trevor Li on 7/07/2015.
 */
public class EndpointDetail {
    public static final String PASSWORD_PROPERTY = "password";

    private long id;
    private long endpointId;
    private String name;
    private String value;
    private Date created;
    private Date updated;

    public EndpointDetail() {}

    public EndpointDetail(long id, long endpointId, String name, String value, Date created, Date updated) {
        this.id = id;
        this.endpointId = endpointId;
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

    public long getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(long endpointId) {
        this.endpointId = endpointId;
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
