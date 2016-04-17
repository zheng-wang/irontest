package io.irontest.models;

import java.util.Date;
import java.util.List;

/**
 * Created by Trevor Li on 6/30/15.
 */
public class Endpoint {
    private long id;
    private long environmentId;
    private String name;
    private String type;
    private String description;
    private String url;
    private String username;
    private String password;
    private Date created;
    private Date updated;

    public Endpoint() {}

    public Endpoint(long id, long environmentId, String name, String type, String description, String url,
                    String username, String password, Date created, Date updated) {
        this.id = id;
        this.environmentId = environmentId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.url = url;
        this.username = username;
        this.password = password;
        this.created = created;
        this.updated = updated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(long environmentId) {
        this.environmentId = environmentId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
