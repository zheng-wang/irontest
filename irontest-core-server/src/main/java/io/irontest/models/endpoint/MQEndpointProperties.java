package io.irontest.models.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.resources.ResourceJsonViews;

@JsonTypeName(EndpointProperties.MQ_ENDPOINT_PROPERTIES)
public class MQEndpointProperties extends EndpointProperties {
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private MQConnectionMode connectionMode = MQConnectionMode.BINDINGS;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String queueManagerName;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String host;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private Integer port;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String svrConnChannelName;

    public MQConnectionMode getConnectionMode() {
        return connectionMode;
    }

    public void setConnectionMode(MQConnectionMode connectionMode) {
        this.connectionMode = connectionMode;
    }

    public String getQueueManagerName() {
        return queueManagerName;
    }

    public void setQueueManagerName(String queueManagerName) {
        this.queueManagerName = queueManagerName;
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

    public String getSvrConnChannelName() {
        return svrConnChannelName;
    }

    public void setSvrConnChannelName(String svrConnChannelName) {
        this.svrConnChannelName = svrConnChannelName;
    }

    /**
     * Used to unify queue manager address display on test step action tab and test case run report.
     * @return
     */
    @JsonProperty
    @JsonView(ResourceJsonViews.TeststepEdit.class)
    public String getQueueManagerAddress() {
        return connectionMode == MQConnectionMode.BINDINGS ?
                queueManagerName : host + ':' + port + '/' + queueManagerName;
    }

    @JsonIgnore
    public void setQueueManagerAddress(String queueManagerAddress) {
        //  do nothing
    }
}
