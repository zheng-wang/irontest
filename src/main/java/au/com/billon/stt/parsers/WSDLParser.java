package au.com.billon.stt.parsers;

import au.com.billon.stt.models.SOAPTeststepProperties;
import au.com.billon.stt.models.TeststepProperties;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/25/15.
 */
public class WSDLParser implements STTParser {
    public String getSampleRequest(TeststepProperties details) {
        SOAPTeststepProperties soapDetails = (SOAPTeststepProperties) details;

        Wsdl wsdl = Wsdl.parse(soapDetails.getWsdlUrl());
        SoapBuilder builder = wsdl.binding().localPart(soapDetails.getWsdlBindingName()).find();
        SoapOperation operation = builder.operation().name(soapDetails.getWsdlOperationName()).find();

        return builder.buildInputMessage(operation);
    }

    public String getAdhocAddress(TeststepProperties details) {
        SOAPTeststepProperties soapDetails = (SOAPTeststepProperties) details;

        Wsdl wsdl = Wsdl.parse(soapDetails.getWsdlUrl());
        SoapBuilder builder = wsdl.binding().localPart(soapDetails.getWsdlBindingName()).find();

        return builder.getServiceUrls().get(0);
    }

    public List<String> getProperties() {
        String[] properties = {"wsdlUrl", "wsdlBindingName", "wsdlOperationName"};
        return Arrays.asList(properties);
    }
}
