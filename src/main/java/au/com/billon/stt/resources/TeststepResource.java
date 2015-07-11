package au.com.billon.stt.resources;

import au.com.billon.stt.db.TeststepDAO;
import au.com.billon.stt.models.Teststep;
import au.com.billon.stt.models.WSDLBinding;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zheng on 11/07/2015.
 */
@Path("/testcases/{testcaseId}/teststeps") @Produces({ MediaType.APPLICATION_JSON })
public class TeststepResource {
    private final TeststepDAO dao;

    public TeststepResource(TeststepDAO dao) {
        this.dao = dao;
    }

    @POST
    public Teststep create(Teststep teststep) {
        //  create sample soap request
        Wsdl wsdl = Wsdl.parse(teststep.getWsdlUrl());
        SoapBuilder builder = wsdl.binding().localPart(teststep.getWsdlBindingName()).find();
        SoapOperation operation = builder.operation().name(teststep.getWsdlOperationName()).find();
        teststep.setRequest(builder.buildInputMessage(operation));

        //  create test step
        long id = dao.insert(teststep);
        teststep.setId(id);
        teststep.setRequest(null);  //  no need to bring request to client at this point
        return teststep;
    }
}
