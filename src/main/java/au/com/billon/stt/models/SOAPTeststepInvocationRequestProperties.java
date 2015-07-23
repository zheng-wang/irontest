package au.com.billon.stt.models;

/**
 * Created by Zheng on 21/07/2015.
 */
public class SOAPTeststepInvocationRequestProperties extends TeststepInvocationRequestProperties {
    private String soapAddress;

    public String getSoapAddress() {
        return soapAddress;
    }

    public void setSoapAddress(String soapAddress) {
        this.soapAddress = soapAddress;
    }
}
