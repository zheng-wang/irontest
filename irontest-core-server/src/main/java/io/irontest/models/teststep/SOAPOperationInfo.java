package io.irontest.models.teststep;

/**
 * Created by Zheng on 26/01/2017.
 */
public class SOAPOperationInfo {
    private String sampleRequest;
    private String soapAction;

    public String getSampleRequest() {
        return sampleRequest;
    }

    public void setSampleRequest(String sampleRequest) {
        this.sampleRequest = sampleRequest;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }
}
