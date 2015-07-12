package au.com.billon.stt.models;

import java.util.Date;
import java.util.List;

/**
 * Created by Zheng on 12/07/2015.
 */
public class SOAPTeststep extends Teststep {
    private String wsdlUrl;
    private String wsdlBindingName;
    private String wsdlOperationName;
    private String soapAddress;

    public SOAPTeststep() {}

    public SOAPTeststep(Teststep teststep) {
        super(teststep.getId(), teststep.getTestcaseId(), teststep.getName(), teststep.getDescription(),
                teststep.getCreated(), teststep.getUpdated(), teststep.getRequest());
    }

    public SOAPTeststep(long id, long testcaseId, String name, String description, Date created, Date updated, String request) {
        super(id, testcaseId, name, description, created, updated, request);
    }

    public String getWsdlUrl() {
        return wsdlUrl;
    }

    public void setWsdlUrl(String wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
    }

    public String getWsdlBindingName() {
        return wsdlBindingName;
    }

    public void setWsdlBindingName(String wsdlBindingName) {
        this.wsdlBindingName = wsdlBindingName;
    }

    public String getWsdlOperationName() {
        return wsdlOperationName;
    }

    public void setWsdlOperationName(String wsdlOperationName) {
        this.wsdlOperationName = wsdlOperationName;
    }

    public String getSoapAddress() {
        return soapAddress;
    }

    public void setSoapAddress(String soapAddress) {
        this.soapAddress = soapAddress;
    }
}
