package io.irontest.models;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.resources.ResourceJsonViews;

public class DataTableCell {
    @JsonView(ResourceJsonViews.DataTableUIGrid.class)
    private long id;
    @JsonView({ResourceJsonViews.DataTableUIGrid.class, ResourceJsonViews.TestcaseExport.class})
    private short rowSequence;
    @JsonView({ResourceJsonViews.DataTableUIGrid.class, ResourceJsonViews.TestcaseExport.class})
    private String value = "";
    @JsonView({ResourceJsonViews.DataTableUIGrid.class, ResourceJsonViews.TestcaseExport.class})
    private Endpoint endpoint;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public short getRowSequence() {
        return rowSequence;
    }

    public void setRowSequence(short rowSequence) {
        this.rowSequence = rowSequence;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }
}
