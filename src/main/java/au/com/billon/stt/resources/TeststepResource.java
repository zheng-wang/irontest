package au.com.billon.stt.resources;

import au.com.billon.stt.db.TeststepDAO;
import au.com.billon.stt.models.SOAPTeststepProperties;
import au.com.billon.stt.models.Teststep;
import au.com.billon.stt.models.TeststepProperties;
import au.com.billon.stt.parsers.ParserFactory;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    public Teststep create(Teststep teststep) throws JsonProcessingException {
        TeststepProperties properties = teststep.getProperties();

        String parserName = "DBInterface";
        if (Teststep.TEST_STEP_TYPE_SOAP.equals(teststep.getType())) {
            parserName = "WSDL";
            String adhocAddress = ParserFactory.getInstance().getParser(parserName).getAdhocAddress(properties);
            ((SOAPTeststepProperties) properties).setSoapAddress(adhocAddress);
        }

        String sampleRequest = ParserFactory.getInstance().getParser(parserName).getSampleRequest(properties);
        teststep.setRequest(sampleRequest);

        long id = dao.insert(teststep);
        teststep.setId(id);
        teststep.setRequest(null);  //  no need to bring request to client at this point
        return teststep;
    }

    @GET
    @Path("{teststepId}")
    public Teststep findById(@PathParam("teststepId") long teststepId) {
        return dao.findById(teststepId);
    }

    @PUT @Path("{teststepId}")
    public Teststep update(Teststep teststep) throws JsonProcessingException {
        dao.update(teststep);
        return findById(teststep.getId());
    }

    @DELETE @Path("{teststepId}")
    public void delete(@PathParam("teststepId") long teststepId) {
        dao.deleteById(teststepId);
    }
}
