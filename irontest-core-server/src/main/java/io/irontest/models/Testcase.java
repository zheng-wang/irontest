package io.irontest.models;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.teststep.Teststep;
import io.irontest.resources.ResourceJsonViews;

import java.util.ArrayList;
import java.util.List;

public class Testcase {
    private long id;
    @JsonView(ResourceJsonViews.TestcaseExport.class)
    private String name;
    private long parentFolderId;
    private String folderPath;
    @JsonView(ResourceJsonViews.TestcaseExport.class)
    private String description;
    @JsonView(ResourceJsonViews.TestcaseExport.class)
    private List<UserDefinedProperty> udps;
    @JsonView(ResourceJsonViews.TestcaseExport.class)
    private List<Teststep> teststeps;
    @JsonView(ResourceJsonViews.TestcaseExport.class)
    private DataTable dataTable;
    @JsonView(ResourceJsonViews.TestcaseExport.class)
    private List<HTTPStubMapping> httpStubMappings = new ArrayList<>();
    @JsonView(ResourceJsonViews.TestcaseExport.class)
    private boolean checkHTTPStubsHitOrder;

    public Testcase() {}

    public Testcase(long id, String name, String description, long parentFolderId, boolean checkHTTPStubsHitOrder) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parentFolderId = parentFolderId;
        this.checkHTTPStubsHitOrder = checkHTTPStubsHitOrder;
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

    public List<UserDefinedProperty> getUdps() {
        return udps;
    }

    public void setUdps(List<UserDefinedProperty> udps) {
        this.udps = udps;
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

    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public List<HTTPStubMapping> getHttpStubMappings() {
        return httpStubMappings;
    }

    public void setHttpStubMappings(List<HTTPStubMapping> httpStubMappings) {
        this.httpStubMappings = httpStubMappings;
    }

    public boolean isCheckHTTPStubsHitOrder() {
        return checkHTTPStubsHitOrder;
    }

    public void setCheckHTTPStubsHitOrder(boolean checkHTTPStubsHitOrder) {
        this.checkHTTPStubsHitOrder = checkHTTPStubsHitOrder;
    }
}
