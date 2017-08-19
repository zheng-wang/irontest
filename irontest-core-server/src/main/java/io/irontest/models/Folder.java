package io.irontest.models;

import java.util.Date;

/**
 * Created by Zheng on 17/09/2016.
 */
public class Folder {
    private long id;
    private String name;
    private String description;
    private Date updated;

    public Folder() {}

    public Folder(long id, String name, String description, Date updated) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
