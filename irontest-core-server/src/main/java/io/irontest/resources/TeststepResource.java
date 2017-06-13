package io.irontest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.core.runner.BasicTeststepRun;
import io.irontest.core.runner.SOAPAPIResponse;
import io.irontest.core.runner.TeststepRunnerFactory;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.endpoint.SOAPEndpointProperties;
import io.irontest.models.teststep.*;
import io.irontest.utils.IronTestUtils;
import io.irontest.utils.XMLUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Zheng on 11/07/2015.
 */
@Path("/testcases/{testcaseId}/teststeps") @Produces({ MediaType.APPLICATION_JSON })
public class TeststepResource {
    private final TeststepDAO teststepDAO;
    private final UtilsDAO utilsDAO;

    public TeststepResource(TeststepDAO teststepDAO, UtilsDAO utilsDAO) {
        this.teststepDAO = teststepDAO;
        this.utilsDAO = utilsDAO;
    }

    @POST
    public Teststep create(Teststep teststep) throws JsonProcessingException {
        preCreationProcess(teststep);

        return teststepDAO.insert(teststep);
    }

    //  adding more info to the teststep object
    private void preCreationProcess(Teststep teststep) {
        //  create sample request
        String sampleRequest = null;
        if (Teststep.TYPE_DB.equals(teststep.getType())){
            sampleRequest = "select * from ? where ?";
        }
        teststep.setRequest(sampleRequest);

        //  create unmanaged endpoint
        if (!Teststep.TYPE_WAIT.equals(teststep.getType())) {
            Endpoint endpoint = new Endpoint();
            endpoint.setName("Unmanaged Endpoint");
            if (Teststep.TYPE_SOAP.equals(teststep.getType())) {
                endpoint.setType(Endpoint.TYPE_SOAP);
                endpoint.setOtherProperties(new SOAPEndpointProperties());
            } else if (Teststep.TYPE_DB.equals(teststep.getType())) {
                endpoint.setType(Endpoint.TYPE_DB);
            } else if (Teststep.TYPE_IIB.equals(teststep.getType())) {
                endpoint.setType(Endpoint.TYPE_MQIIB);
            } else if (Teststep.TYPE_MQ.equals(teststep.getType())) {
                endpoint.setType(Endpoint.TYPE_MQIIB);
            }
            teststep.setEndpoint(endpoint);
        }

        //  set initial/default property values (in the Properties sub-class)
        if (Teststep.TYPE_SOAP.equals(teststep.getType())) {
            teststep.setOtherProperties(new SOAPTeststepProperties());
        } else if (Teststep.TYPE_MQ.equals(teststep.getType())) {
            teststep.setOtherProperties(new MQTeststepProperties());
        } else if (Teststep.TYPE_WAIT.equals(teststep.getType())) {
            teststep.setOtherProperties(new WaitTeststepProperties(1));   //  there is no point to wait for 0 seconds
        }
    }

    private void populateParametersInWrapper(TeststepWrapper wrapper) throws Exception {
        Teststep teststep = wrapper.getTeststep();
        if (Teststep.TYPE_DB.equals(teststep.getType())) {
            boolean isSQLRequestSingleSelectStatement;
            try {
                isSQLRequestSingleSelectStatement = IronTestUtils.isSQLRequestSingleSelectStatement(
                        (String) teststep.getRequest());
            } catch (Exception e) {
                //  the SQL script is invalid, so it can't be a single select statement
                //  swallow the exception to avoid premature error message on UI (user is still editing the SQL script)
                //  the exception will popup on UI when user executes the script (the script will be reparsed at that time)
                isSQLRequestSingleSelectStatement = false;
            }
            wrapper.getParameters().put("isSQLRequestSingleSelectStatement", isSQLRequestSingleSelectStatement);
        }
    }

    @GET @Path("{teststepId}")
    public TeststepWrapper findById(@PathParam("teststepId") long teststepId) throws Exception {
        TeststepWrapper wrapper = new TeststepWrapper();
        Teststep teststep = teststepDAO.findById(teststepId);
        wrapper.setTeststep(teststep);
        populateParametersInWrapper(wrapper);

        return wrapper;
    }

    @PUT @Path("{teststepId}")
    public TeststepWrapper update(Teststep teststep) throws Exception {
        Thread.sleep(100);  //  workaround for Chrome 44 to 48's 'Failed to load response data' problem (no such problem in Chrome 49)
        TeststepWrapper wrapper = new TeststepWrapper();
        teststep = teststepDAO.update(teststep);
        wrapper.setTeststep(teststep);
        populateParametersInWrapper(wrapper);

        return wrapper;
    }

    @DELETE @Path("{teststepId}")
    public void delete(@PathParam("teststepId") long teststepId) {
        teststepDAO.deleteById(teststepId);
    }

    /**
     * Run a test step individually (not as part of test case running).
     * @param teststep
     * @return API response
     */
    @POST @Path("{teststepId}/run")
    public BasicTeststepRun run(Teststep teststep) throws Exception {
        Thread.sleep(100); // workaround for Chrome 44 to 48's 'Failed to load response data' problem (no such problem in Chrome 49)

        BasicTeststepRun basicTeststepRun = TeststepRunnerFactory.getInstance().newTeststepRunner(teststep, teststepDAO, utilsDAO, null).run();

        if (Teststep.TYPE_SOAP.equals(teststep.getType())) {
            //  for better displaying SOAP response in browser, transform XML to be pretty-printed
            SOAPAPIResponse soapAPIResponse = (SOAPAPIResponse) basicTeststepRun.getResponse();
            String httpResponseContentType = IronTestUtils.getFirstHTTPHeaderValue(
                    soapAPIResponse.getHttpHeaders(), HttpHeaders.CONTENT_TYPE);
            if (httpResponseContentType != null &&
                    (MediaType.TEXT_XML_TYPE.isCompatible(MediaType.valueOf(httpResponseContentType)) ||
                            httpResponseContentType.contains("application/soap+xml"))) {
                soapAPIResponse.setHttpBody(XMLUtils.prettyPrintXML(soapAPIResponse.getHttpBody()));
            }
        }

        return basicTeststepRun;
    }

    /**
     * Save the uploaded file as Teststep.request.
     * @param teststepId
     * @param inputStream
     * @param contentDispositionHeader
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @POST @Path("{teststepId}/uploadRequestFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Teststep uploadRequestFile(@PathParam("teststepId") long teststepId,
                                      @FormDataParam("file") InputStream inputStream,
                                      @FormDataParam("file") FormDataContentDisposition contentDispositionHeader)
            throws IOException, InterruptedException {
        Thread.sleep(100);  //  workaround for Chrome 44 to 48's 'Failed to load response data' problem (no such problem in Chrome 49)
        return teststepDAO.setRequestFile(teststepId, contentDispositionHeader.getFileName(), inputStream);
    }

    /**
     * Download Teststep.request as a file.
     * @param teststepId
     * @return
     * @throws IOException
     */
    @GET @Path("{teststepId}/downloadRequestFile")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadRequestFile(@PathParam("teststepId") long teststepId) throws IOException {
        Teststep teststep = teststepDAO.findById(teststepId);
        teststep.setRequest(teststepDAO.getBinaryRequestById(teststep.getId()));
        String filename = "UnknownFilename";
        if (teststep.getOtherProperties() instanceof MQTeststepProperties) {
            MQTeststepProperties properties = (MQTeststepProperties) teststep.getOtherProperties();
            filename = properties.getMessageFilename();
        }
        return Response.ok(teststep.getRequest())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .build();
    }
}
