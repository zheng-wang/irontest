package io.irontest.models.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.Environment;
import io.irontest.resources.ResourceJsonViews;

public class Endpoint {
    public static final String TYPE_HTTP = "HTTP";
    public static final String TYPE_SOAP = "SOAP";
    public static final String TYPE_DB = "DB";
    public static final String TYPE_JMS = "JMS";
    public static final String TYPE_FTP = "FTP";
    public static final String TYPE_MQ = "MQ";
    public static final String TYPE_IIB = "IIB";
    public static final String TYPE_AMQP = "AMQP";
    public static final String TYPE_MQTT = "MQTT";

    @JsonView(ResourceJsonViews.TeststepEdit.class)
    private long id;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.DataTableUIGrid.class})
    private Environment environment;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.DataTableUIGrid.class,
            ResourceJsonViews.TestcaseExport.class})
    private String name;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String type;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String description;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String url;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String host;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private Integer port;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String username;
    @JsonView(ResourceJsonViews.TeststepEdit.class)
    private String password;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private EndpointProperties otherProperties = new EndpointProperties();

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

    @JsonProperty
    @JsonView(ResourceJsonViews.TeststepEdit.class)
    public String getConstructedUrl() {
        return getOtherProperties().constructUrl(this.host, this.port);
    }

    @JsonIgnore
    public void setConstructedUrl(String constructedUrl) {
        //  do nothing
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public EndpointProperties getOtherProperties() {
        return otherProperties;
    }

    public void setOtherProperties(EndpointProperties otherProperties) {
        this.otherProperties = otherProperties;
    }

    @JsonIgnore
    public boolean isManaged() {
        return this.environment != null;
    }
}
