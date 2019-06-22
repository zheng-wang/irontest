package io.irontest.models;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.resources.ResourceJsonViews;

import java.util.List;

public class Environment {
    private long id;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.DataTableUIGrid.class})
    private String name;
    private String description;
    private List<Endpoint> endpoints;

    public Environment() {}

    public Environment(long id, String name, String description, List<Endpoint> endpoints) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.endpoints = endpoints;
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

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }
}
