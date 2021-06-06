package io.irontest.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.db.AssertionDAO;
import io.irontest.db.DataTableDAO;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UserDefinedPropertyDAO;
import io.irontest.models.DataTable;
import io.irontest.models.TestResult;
import io.irontest.models.UserDefinedProperty;
import io.irontest.models.assertion.*;
import io.irontest.models.teststep.MQRFH2Header;
import io.irontest.utils.IronTestUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class AssertionResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssertionResource.class);

    private final UserDefinedPropertyDAO udpDAO;
    private final TeststepDAO teststepDAO;
    private final DataTableDAO dataTableDAO;
    private final AssertionDAO assertionDAO;

    public AssertionResource(UserDefinedPropertyDAO udpDAO, TeststepDAO teststepDAO, DataTableDAO dataTableDAO,
                             AssertionDAO assertionDAO) {
        this.udpDAO = udpDAO;
        this.teststepDAO = teststepDAO;
        this.dataTableDAO = dataTableDAO;
        this.assertionDAO = assertionDAO;
    }

    /**
     * This is a stateless operation, i.e. not persisting anything in database.
     * @param assertionVerificationRequest
     * @return
     */
    @POST @Path("assertions/{assertionId}/verify")
    @PermitAll
    public AssertionVerificationResult verify(AssertionVerificationRequest assertionVerificationRequest) throws IOException {
        Assertion assertion = assertionVerificationRequest.getAssertion();

        //  populate xsd file bytes for JSONValidAgainstJSONSchema or XMLValidAgainstXSD assertion which are not passed from UI to this method
        if (Assertion.TYPE_JSON_VALID_AGAINST_JSON_SCHEMA.equals(assertion.getType()) ||
                Assertion.TYPE_XML_VALID_AGAINST_XSD.equals(assertion.getType())) {
            assertion.setOtherProperties(assertionDAO.findById(assertion.getId()).getOtherProperties());
        }

        //  gather referenceable string properties
        long testcaseId = teststepDAO.findTestcaseIdById(assertion.getTeststepId());
        List<UserDefinedProperty> testcaseUDPs = udpDAO.findByTestcaseId(testcaseId);
        Map<String, String> referenceableStringProperties = IronTestUtils.udpListToMap(testcaseUDPs);
        Set<String> udpNames = referenceableStringProperties.keySet();
        DataTable dataTable = dataTableDAO.getTestcaseDataTable(testcaseId, true);
        if (dataTable.getRows().size() > 0) {
            IronTestUtils.checkDuplicatePropertyNameBetweenDataTableAndUPDs(udpNames, dataTable);
            referenceableStringProperties.putAll(dataTable.getStringPropertiesInRow(0));
        }

        AssertionVerifier assertionVerifier = AssertionVerifierFactory.getInstance().create(
                assertion, referenceableStringProperties);
        Object assertionInput = assertionVerificationRequest.getInput();
        if (Assertion.TYPE_HAS_AN_MQRFH2_FOLDER_EQUAL_TO_XML.equals(assertion.getType())) {
            assertionInput = new ObjectMapper().convertValue(assertionInput, MQRFH2Header.class);
        }
        AssertionVerificationResult result;
        try {
            result = assertionVerifier.verify(assertionInput);
        } catch (Exception e) {
            LOGGER.error("Failed to verify assertion", e);
            result = new AssertionVerificationResult();
            result.setResult(TestResult.FAILED);
            result.setError(e.getMessage());
        }
        return result;
    }

    /**
     * Save the uploaded XSD file (or zip file) into the (XMLValidAgainstXSD) assertion.
     * Use @POST instead of @PUT because ng-file-upload seems not working with PUT.
     * @param assertionId
     * @param inputStream
     * @param contentDispositionHeader
     * @return
     */
    @POST @Path("assertions/{assertionId}/xsdFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @PermitAll
    public void saveXSDFile(@PathParam("assertionId") long assertionId,
                                    @FormDataParam("file") InputStream inputStream,
                                    @FormDataParam("file") FormDataContentDisposition contentDispositionHeader) throws IOException {
        //  check the file
        String fileName = contentDispositionHeader.getFileName();
        if (!(fileName.toLowerCase().endsWith(".xsd") || fileName.toLowerCase().endsWith(".zip"))) {
            throw new IllegalArgumentException("Only XSD file and Zip file are supported.");
        }

        XMLValidAgainstXSDAssertionProperties properties = new XMLValidAgainstXSDAssertionProperties();
        properties.setFileName(fileName);
        byte[] fileBytes;
        try {
            fileBytes = IOUtils.toByteArray(inputStream);
        } finally {
            inputStream.close();
        }
        properties.setFileBytes(fileBytes);
        assertionDAO.updateOtherProperties(assertionId, properties);
    }

    /**
     * Download the XSD file (or zip file) from the (XMLValidAgainstXSD) assertion.
     * @param assertionId
     * @return
     */
    @GET @Path("assertions/{assertionId}/xsdFile")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getXSDFile(@PathParam("assertionId") long assertionId) {
        Assertion assertion = assertionDAO.findById(assertionId);
        XMLValidAgainstXSDAssertionProperties assertionProperties =
                (XMLValidAgainstXSDAssertionProperties) assertion.getOtherProperties();
        String fileName = assertionProperties.getFileName();
        return Response.ok(assertionProperties.getFileBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .build();
    }

    /**
     * Save the uploaded JSON Schema file into the (JSONValidAgainstJSONSchema) assertion.
     * Use @POST instead of @PUT because ng-file-upload seems not working with PUT.
     * @param assertionId
     * @param inputStream
     * @param contentDispositionHeader
     * @return
     */
    @POST @Path("assertions/{assertionId}/jsonSchemaFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @PermitAll
    public void saveJSONSchemaFile(@PathParam("assertionId") long assertionId,
                            @FormDataParam("file") InputStream inputStream,
                            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader) throws IOException {
        //  check the file
        String fileName = contentDispositionHeader.getFileName();
        if (!fileName.toLowerCase().endsWith(".json")) {
            throw new IllegalArgumentException("Only .json file is supported.");
        }

        JSONValidAgainstJSONSchemaAssertionProperties properties = new JSONValidAgainstJSONSchemaAssertionProperties();
        properties.setFileName(fileName);
        byte[] fileBytes;
        try {
            fileBytes = IOUtils.toByteArray(inputStream);
        } finally {
            inputStream.close();
        }
        properties.setFileBytes(fileBytes);
        assertionDAO.updateOtherProperties(assertionId, properties);
    }

    /**
     * Download the JSON Schema file from the (JSONValidAgainstJSONSchema) assertion.
     * @param assertionId
     * @return
     */
    @GET @Path("assertions/{assertionId}/jsonSchemaFile")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getJSONSchemaFile(@PathParam("assertionId") long assertionId) {
        Assertion assertion = assertionDAO.findById(assertionId);
        JSONValidAgainstJSONSchemaAssertionProperties assertionProperties =
                (JSONValidAgainstJSONSchemaAssertionProperties) assertion.getOtherProperties();
        String fileName = assertionProperties.getFileName();
        return Response.ok(assertionProperties.getFileBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .build();
    }
}