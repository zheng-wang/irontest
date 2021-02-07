package io.irontest.models.endpoint;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.resources.ResourceJsonViews;

@JsonTypeName(EndpointProperties.JMS_SOLACE_ENDPOINT_PROPERTIES)
public class JMSSolaceEndpointProperties extends JMSEndpointProperties {
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String vpn;

    public String getVpn() {
        return vpn;
    }

    public void setVpn(String vpn) {
        this.vpn = vpn;
    }

    @Override
    public String constructUrl(String host, Integer port) {
        return host + ':' + port + '/' + vpn;
    }
}
