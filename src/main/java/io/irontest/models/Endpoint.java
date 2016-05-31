package io.irontest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Date;

/**
 * Created by Trevor Li on 6/30/15.
 */
public class Endpoint {
    public static final String ENDPOINT_TYPE_SOAP = "SOAP";
    public static final String ENDPOINT_TYPE_DB = "DB";
    public static final String ENDPOINT_TYPE_MQIIB = "MQ/IIB";
    private long id;
    private Environment environment;
    private String name;
    private String type;
    private String description;
    private String url;    //  can be SOAP address, JDBC URL, etc.; not used by IIB endpoint
    private String username;           //  not used by MQ/IIB endpoint
    private String password;           //  not used by MQ/IIB endpoint
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type", visible = true, defaultImpl = Properties.class)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MQIIBEndpointProperties.class, name = Endpoint.ENDPOINT_TYPE_MQIIB)})
    private Properties otherProperties;
    private Date created;
    private Date updated;

    public Endpoint() {}

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

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Properties getOtherProperties() {
        return otherProperties;
    }

    public void setOtherProperties(Properties otherProperties) {
        this.otherProperties = otherProperties;
    }

    @JsonIgnore
    public boolean isManaged() {
        return this.environment != null;
    }
}
