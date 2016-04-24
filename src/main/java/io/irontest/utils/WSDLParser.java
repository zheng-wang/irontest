package io.irontest.utils;

import io.irontest.models.SOAPTeststepProperties;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;

/**
 * Created by Trevor Li on 7/25/15.
 */
public class WSDLParser {
    public static String getSampleRequest(SOAPTeststepProperties properties) {
        Wsdl wsdl = Wsdl.parse(properties.getWsdlUrl());
        SoapBuilder builder = wsdl.binding().localPart(properties.getWsdlBindingName()).find();
        SoapOperation operation = builder.operation().name(properties.getWsdlOperationName()).find();

        return builder.buildInputMessage(operation);
    }

    public static String getAdhocAddress(SOAPTeststepProperties properties) {
        Wsdl wsdl = Wsdl.parse(properties.getWsdlUrl());
        SoapBuilder builder = wsdl.binding().localPart(properties.getWsdlBindingName()).find();

        return builder.getServiceUrls().get(0);
    }
}
