package au.com.billon.stt.models;

/**
 * Created by Zheng on 12/07/2015.
 */
public class TeststepInvocation {
    private String soapAddress;
    private String request;

    public String getSoapAddress() {
        return soapAddress;
    }

    public void setSoapAddress(String soapAddress) {
        this.soapAddress = soapAddress;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
