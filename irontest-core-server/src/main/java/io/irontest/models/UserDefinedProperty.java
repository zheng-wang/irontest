package io.irontest.models;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.resources.ResourceJsonViews;

public class UserDefinedProperty {
    private long id;
    private short sequence;
    @JsonView(ResourceJsonViews.TestcaseExport.class)
    private String name;
    @JsonView(ResourceJsonViews.TestcaseExport.class)
    private String value;

    public UserDefinedProperty() {}

    public UserDefinedProperty(long id, short sequence, String name, String value) {
        this.id = id;
        this.sequence = sequence;
        this.name = name;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public short getSequence() {
        return sequence;
    }

    public void setSequence(short sequence) {
        this.sequence = sequence;
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
}
