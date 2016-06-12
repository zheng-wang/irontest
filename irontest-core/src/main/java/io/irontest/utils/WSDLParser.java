package io.irontest.utils;

import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;

/**
 * Created by Trevor Li on 7/25/15.
 */
public class WSDLParser {
    public static String getSampleRequest(String wsdlUrl, String bindingName, String operationName) {
        Wsdl wsdl = Wsdl.parse(wsdlUrl);
        SoapBuilder builder = wsdl.binding().localPart(bindingName).find();
        SoapOperation operation = builder.operation().name(operationName).find();

        return builder.buildInputMessage(operation);
    }
}
