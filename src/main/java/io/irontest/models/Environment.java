package io.irontest.models;

import java.util.Date;
import java.util.List;

/**
 * Created by Trevor Li on 7/5/15.
 */
public class Environment {
    private long id;
    private String name;
    private String description;
    private List<EnvEntry> entries;
    private Date created;
    private Date updated;

    public Environment() {
    }

    public Environment(long id, String name, String description, Date created, Date updated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.created = created;
        this.updated = updated;
    }

    public long getId() {
        return id;
    }

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

    public List<EnvEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<EnvEntry> entries) {
        this.entries = entries;
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
