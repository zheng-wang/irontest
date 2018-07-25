package io.irontest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.core.runner.*;
import io.irontest.db.DataTableDAO;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UserDefinedPropertyDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.AppInfo;
import io.irontest.models.DataTable;
import io.irontest.models.Properties;
import io.irontest.models.UserDefinedProperty;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.*;
import io.irontest.utils.IronTestUtils;
import io.irontest.utils.XMLUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static io.irontest.IronTestConstants.IMPLICIT_PROPERTY_DATE_TIME_FORMAT;
import static io.irontest.IronTestConstants.IMPLICIT_PROPERTY_NAME_TEST_STEP_START_TIME;

@Path("/testcases/{testcaseId}/teststeps") @Produces({ MediaType.APPLICATION_JSON })
public class TeststepResource {
    private final AppInfo appInfo;
    private final TeststepDAO teststepDAO;
    private final UserDefinedPropertyDAO udpDAO;
    private final UtilsDAO utilsDAO;
    private final DataTableDAO dataTableDAO;

    public TeststepResource(AppInfo appInfo, TeststepDAO teststepDAO, UserDefinedPropertyDAO udpDAO, UtilsDAO utilsDAO,
                            DataTableDAO dataTableDAO) {
        this.appInfo = appInfo;
        this.teststepDAO = teststepDAO;
        this.udpDAO = udpDAO;
        this.utilsDAO = utilsDAO;
        this.dataTableDAO = dataTableDAO;
    }

    @POST
    @PermitAll
    public Teststep create(Teststep teststep) throws JsonProcessingException {
        preCreationProcess(teststep);

        long teststepId = teststepDAO.insert(teststep, appInfo.getAppMode());

        return teststepDAO.findById(teststepId);
    }

    //  adding more info to the teststep object
    private void preCreationProcess(Teststep teststep) {
        //  create sample request
        String sampleRequest = null;
        if (Teststep.TYPE_DB.equals(teststep.getType())){
            sampleRequest = "select * from ? where ?";
        }
        teststep.setRequest(sampleRequest);

        //  set initial/default property values (in the Properties sub-class)
        Properties otherProperties = new Properties();
        switch (teststep.getType()) {
            case Teststep.TYPE_SOAP:
                otherProperties = new SOAPTeststepProperties();
                break;
            case Teststep.TYPE_HTTP:
                otherProperties = new HTTPTeststepProperties();
                break;
            case Teststep.TYPE_MQ:
                otherProperties = new MQTeststepProperties();
                break;
            case Teststep.TYPE_WAIT:
                otherProperties = new WaitTeststepProperties(1000);  //  there is no point to wait for 0 milliseconds
                break;
            default:
                break;
        }
        teststep.setOtherProperties(otherProperties);
    }

    private void populateParametersInWrapper(TeststepWrapper wrapper) {
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
    @PermitAll
    public TeststepWrapper update(Teststep teststep) throws Exception {
        Thread.sleep(100);  //  workaround for Chrome's 'Failed to load response data' problem (still exist in Chrome 65)

        TeststepWrapper wrapper = new TeststepWrapper();
        teststep = teststepDAO.update(teststep);
        wrapper.setTeststep(teststep);
        populateParametersInWrapper(wrapper);

        return wrapper;
    }

    @DELETE @Path("{teststepId}")
    @PermitAll
    public void delete(@PathParam("teststepId") long teststepId) {
        teststepDAO.deleteById(teststepId);
    }

    /**
     * Run a test step individually (not as part of test case running).
     * This is a stateless operation, i.e. not persisting anything in database.
     * @param teststep
     * @return
     */
    @POST @Path("{teststepId}/run")
    @PermitAll
    public BasicTeststepRun run(Teststep teststep) throws Exception {
        Thread.sleep(100);  //  workaround for Chrome's 'Failed to load response data' problem (still exist in Chrome 65)

        //  gather referenceable string properties and endpoint properties
        List<UserDefinedProperty> testcaseUDPs = udpDAO.findByTestcaseId(teststep.getTestcaseId());
        Map<String, String> referenceableStringProperties = IronTestUtils.udpListToMap(testcaseUDPs);
        Set<String> udpNames = referenceableStringProperties.keySet();
        referenceableStringProperties.put(IMPLICIT_PROPERTY_NAME_TEST_STEP_START_TIME,
                IMPLICIT_PROPERTY_DATE_TIME_FORMAT.format(new Date()));
        DataTable dataTable = dataTableDAO.getTestcaseDataTable(teststep.getTestcaseId(), true);
        Map<String, Endpoint> referenceableEndpointProperties = new HashMap<>();
        if (dataTable.getRows().size() > 0) {
            IronTestUtils.checkDuplicatePropertyNameBetweenDataTableAndUPDs(udpNames, dataTable);
            referenceableStringProperties.putAll(dataTable.getStringPropertiesInRow(0));
            referenceableEndpointProperties.putAll(dataTable.getEndpointPropertiesInRow(0));
        }

        //  run the test step
        TeststepRunner teststepRunner = TeststepRunnerFactory.getInstance().newTeststepRunner(
                teststep, teststepDAO, utilsDAO, referenceableStringProperties, referenceableEndpointProperties, null);
        BasicTeststepRun basicTeststepRun = teststepRunner.run();

        //  for better display in browser, transform JSON/XML response to be pretty-printed
        switch (teststep.getType()) {
            case Teststep.TYPE_SOAP:
                HTTPAPIResponse soapAPIResponse = (HTTPAPIResponse) basicTeststepRun.getResponse();
                soapAPIResponse.setHttpBody(XMLUtils.prettyPrintXML(soapAPIResponse.getHttpBody()));
                break;
            case Teststep.TYPE_HTTP:
                HTTPAPIResponse httpAPIResponse = (HTTPAPIResponse) basicTeststepRun.getResponse();
                httpAPIResponse.setHttpBody(IronTestUtils.prettyPrintJSONOrXML(httpAPIResponse.getHttpBody()));
                break;
            case Teststep.TYPE_MQ:
                if (Teststep.ACTION_DEQUEUE.equals(teststep.getAction())) {
                    MQAPIResponse mqAPIResponse = (MQAPIResponse) basicTeststepRun.getResponse();
                    mqAPIResponse.setValue(IronTestUtils.prettyPrintJSONOrXML((String) mqAPIResponse.getValue()));
                }
                break;
            default:
                break;
        }

        return basicTeststepRun;
    }

    /**
     * Save the uploaded file as Teststep.request.
     * Use @POST instead of @PUT because ng-file-upload seems not working with PUT.
     * @param teststepId
     * @param inputStream
     * @param contentDispositionHeader
     * @return
     */
    @POST @Path("{teststepId}/requestFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @PermitAll
    public Teststep saveRequestFile(@PathParam("teststepId") long teststepId,
                                    @FormDataParam("file") InputStream inputStream,
                                    @FormDataParam("file") FormDataContentDisposition contentDispositionHeader) throws IOException {
        return teststepDAO.setRequestFile(teststepId, contentDispositionHeader.getFileName(), inputStream);
    }

    /**
     * Download Teststep.request as a file.
     * @param teststepId
     * @return
     */
    @GET @Path("{teststepId}/requestFile")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getRequestFile(@PathParam("teststepId") long teststepId) {
        Teststep teststep = teststepDAO.findById(teststepId);
        teststep.setRequest(teststepDAO.getBinaryRequestById(teststep.getId()));
        String filename = teststep.getRequestFilename() == null ? "UnknownFilename" : teststep.getRequestFilename();
        return Response.ok(teststep.getRequest())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .build();
    }

    @POST @Path("{teststepId}/useEndpointProperty")
    @PermitAll
    public Teststep useEndpointProperty(Teststep teststep) {
        teststepDAO.useEndpointProperty(teststep);

        return teststepDAO.findById(teststep.getId());
    }

    @POST @Path("{teststepId}/useDirectEndpoint")
    @PermitAll
    public Teststep useDirectEndpoint(Teststep teststep) throws JsonProcessingException {
        teststepDAO.useDirectEndpoint(teststep, appInfo.getAppMode());

        return teststepDAO.findById(teststep.getId());
    }
}
