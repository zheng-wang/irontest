package io.irontest.models;

import java.util.Date;

/**
 * Created by Trevor Li on 7/5/15.
 */
public class EnvEntry {
    private long id;
    private long environmentId;
    private long intfaceId;
    private long endpointId;
    private Environment environment;
    private Intface intface;
    private Endpoint endpoint;
    private Date created;
    private Date updated;

    public EnvEntry() {
    }

    public EnvEntry(long id, long environmentId, long intfaceId, long endpointId, Date created, Date updated) {
        this.id = id;
        this.environmentId = environmentId;
        this.intfaceId = intfaceId;
        this.endpointId = endpointId;
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

    public long getIntfaceId() {
        return intfaceId;
    }

    public void setIntfaceId(long intfaceId) {
        this.intfaceId = intfaceId;
    }

    public long getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(long endpointId) {
        this.endpointId = endpointId;
    }

    public Environment getEnvironment() { return environment; }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Intface getIntface() {
        return intface;
    }

    public void setIntface(Intface intface) {
        this.intface = intface;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public Date getCreated() { return created; }

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
