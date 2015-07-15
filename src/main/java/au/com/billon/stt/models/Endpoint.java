package au.com.billon.stt.models;

import java.util.Date;

/**
 * Created by Trevor Li on 6/30/15.
 */
public class Endpoint {
    private long id;
    private String name;
    private String description;
    private String handler;
    private Date created;
    private Date updated;

    public Endpoint() {
    }

    public Endpoint(long id, String name, String description, String handler, Date created, Date updated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.handler = handler;
        this.created = created;
        this.updated = updated;
    }

    public long getId() { return id; }

    public void setId(long id) {
        this.id = id;
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

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
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
