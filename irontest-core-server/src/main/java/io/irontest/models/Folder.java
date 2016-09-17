package io.irontest.models;

import java.util.Date;

/**
 * Created by Zheng on 17/09/2016.
 */
public class Folder {
    private long id;
    private String name;
    private Date created;
    private Date updated;

    public Folder() {}

    public Folder(long id, String name, Date created, Date updated) {
        this.id = id;
        this.name = name;
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
