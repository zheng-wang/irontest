package io.irontest.models;

import io.irontest.models.teststep.Teststep;

import java.util.Date;
import java.util.List;

/**
 * Created by Zheng on 1/07/2015.
 */
public class Testcase {
    private long id;
    private String name;
    private long parentFolderId;
    private String folderPath;
    private String description;
    private Date updated;
    private List<Teststep> teststeps;

    public Testcase() {}

    public Testcase(long id, String name, String description, long parentFolderId, Date updated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parentFolderId = parentFolderId;
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

    public List<Teststep> getTeststeps() {
        return teststeps;
    }

    public void setTeststeps(List<Teststep> teststeps) {
        this.teststeps = teststeps;
    }

    public long getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(long parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
}
