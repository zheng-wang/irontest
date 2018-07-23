package io.irontest.models.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.Properties;
import io.irontest.resources.ResourceJsonViews;

@JsonView(ResourceJsonViews.TestcaseExport.class)
public class MQEndpointProperties extends Properties {
    private MQConnectionMode connectionMode = MQConnectionMode.BINDINGS;
    private String queueManagerName;
    private String host;
    private Integer port;
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
    public String getQueueManagerAddress() {
        return connectionMode == MQConnectionMode.BINDINGS ?
                queueManagerName : host + ':' + port + '/' + queueManagerName;
    }

    @JsonIgnore
    public void setQueueManagerAddress(String queueManagerAddress) {
        //  do nothing
    }
}
