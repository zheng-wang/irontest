package io.irontest.models.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.Environment;
import io.irontest.models.Properties;
import io.irontest.resources.ResourceJsonViews;

public class Endpoint {
    public static final String TYPE_HTTP = "HTTP";
    public static final String TYPE_SOAP = "SOAP";
    public static final String TYPE_FTP = "FTP";
    public static final String TYPE_DB = "DB";
    public static final String TYPE_MQ = "MQ";
    public static final String TYPE_IIB = "IIB";
    public static final String TYPE_AMQP = "AMQP";

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
    private String url;                //  can be SOAP address, JDBC URL, etc.; not used by MQ or IIB endpoint
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String username;
    @JsonView(ResourceJsonViews.TeststepEdit.class)
    private String password;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type", visible = true, defaultImpl = Properties.class)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = SOAPEndpointProperties.class, name = Endpoint.TYPE_SOAP),
            @JsonSubTypes.Type(value = FTPEndpointProperties.class, name = Endpoint.TYPE_FTP),
            @JsonSubTypes.Type(value = MQEndpointProperties.class, name = Endpoint.TYPE_MQ),
            @JsonSubTypes.Type(value = IIBEndpointProperties.class, name = Endpoint.TYPE_IIB)
    })
    private Properties otherProperties = new Properties();

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
