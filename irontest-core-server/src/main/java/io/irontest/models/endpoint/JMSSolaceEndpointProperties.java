package io.irontest.models.endpoint;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(EndpointProperties.JMS_SOLACE_ENDPOINT_PROPERTIES)
public class JMSSolaceEndpointProperties extends JMSEndpointProperties {
    private String vpn;

    public String getVpn() {
        return vpn;
    }

    public void setVpn(String vpn) {
        this.vpn = vpn;
    }
}
