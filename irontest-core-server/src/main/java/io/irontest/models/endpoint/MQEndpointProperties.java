package io.irontest.models.endpoint;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.resources.ResourceJsonViews;

@JsonTypeName(EndpointProperties.MQ_ENDPOINT_PROPERTIES)
public class MQEndpointProperties extends EndpointProperties {
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private MQConnectionMode connectionMode = MQConnectionMode.BINDINGS;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String queueManagerName;

    //  only meaningful when connection mode is Client
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

    public String getSvrConnChannelName() {
        return svrConnChannelName;
    }

    public void setSvrConnChannelName(String svrConnChannelName) {
        this.svrConnChannelName = svrConnChannelName;
    }

    @Override
    public String constructUrl(String host, Integer port) {
        return connectionMode == MQConnectionMode.BINDINGS ?
                queueManagerName : host + ':' + port + '/' + queueManagerName;
    }
}
