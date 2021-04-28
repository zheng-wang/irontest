package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.core.teststep.MQTeststepActionDataBackup;
import io.irontest.models.AppMode;
import io.irontest.models.HTTPMethod;
import io.irontest.models.Properties;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.IntegerEqualAssertionProperties;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.propertyextractor.PropertyExtractor;
import io.irontest.models.teststep.*;
import io.irontest.utils.IronTestUtils;
import org.apache.commons.io.IOUtils;
import org.jdbi.v3.sqlobject.config.RegisterColumnMapper;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

@RegisterRowMapper(TeststepMapper.class)
public interface TeststepDAO extends CrossReferenceDAO {
    String STEP_MOVE_DIRECTION_UP = "up";
    String STEP_MOVE_DIRECTION_DOWN = "down";

    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS teststep_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS teststep (" +
            "id BIGINT DEFAULT teststep_sequence.NEXTVAL PRIMARY KEY, testcase_id BIGINT NOT NULL, " +
            "sequence SMALLINT NOT NULL, name VARCHAR(200) NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "type VARCHAR(20) NOT NULL, description CLOB, action VARCHAR(50), endpoint_id BIGINT, " +
            "endpoint_property VARCHAR(200), request BLOB, request_type VARCHAR(20) NOT NULL DEFAULT 'Text', " +
            "request_filename VARCHAR(200), api_request CLOB, other_properties CLOB, step_data_backup CLOB, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (endpoint_id) REFERENCES endpoint(id), " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE, " +
            "CONSTRAINT TESTSTEP_UNIQUE_SEQUENCE_CONSTRAINT UNIQUE(testcase_id, sequence), " +
            "CONSTRAINT TESTSTEP_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(testcase_id, name), " +
            "CONSTRAINT TESTSTEP_MANDATORY_ENDPOINT_CONSTRAINT CHECK(" +
                "(TYPE NOT IN ('" + Teststep.TYPE_WAIT + "') AND (ENDPOINT_ID IS NOT NULL OR ENDPOINT_PROPERTY IS NOT NULL)) OR " +
                "TYPE IN ('" + Teststep.TYPE_WAIT + "'))," +
            "CONSTRAINT TESTSTEP_EXCLUSIVE_ENDPOINT_SOURCE_CONSTRAINT CHECK(" +
                "endpoint_id is null OR endpoint_property is null))")
    void createTableIfNotExists();

    /**
     * This method considers test step insertion from both Create (test step) button on UI and test case duplicating.
     * @param teststep
     * @param request
     * @param endpointId
     * @return
     */
    @SqlUpdate("insert into teststep (testcase_id, sequence, type, request, api_request, endpoint_id, " +
            "other_properties) values (:t.testcaseId, " +
              "(select coalesce(max(sequence), 0) + 1 from teststep where testcase_id = :t.testcaseId), " +
            ":t.type, :request, :apiRequest, :endpointId, :t.otherProperties)")
    @GetGeneratedKeys
    long _insertWithoutName(@BindBean("t") Teststep teststep, @Bind("request") Object request,
                            @Bind("endpointId") Long endpointId, @Bind("apiRequest") String apiRequest);

    @SqlUpdate("insert into teststep (testcase_id, sequence, name, type, description, action, request, request_type, " +
            "request_filename, api_request, endpoint_id, endpoint_property, other_properties) values (:t.testcaseId, " +
            "select coalesce(max(sequence), 0) + 1 from teststep where testcase_id = :t.testcaseId, :t.name, " +
            ":t.type, :t.description, :t.action, :request, :requestType, :t.requestFilename, :apiRequest, " +
            ":endpointId, :t.endpointProperty, :t.otherProperties)")
    @GetGeneratedKeys
    long _insertWithName(@BindBean("t") Teststep teststep, @Bind("request") byte[] request,
                         @Bind("requestType") String requestType, @Bind("apiRequest") String apiRequest,
                         @Bind("endpointId") Long endpointId);

    @SqlUpdate("update teststep set name = :name where id = :id")
    void updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @Transaction
    default long insert(Teststep teststep, AppMode appMode) throws JsonProcessingException {
        //  set initial/default values
        Properties otherProperties = new Properties();
        APIRequest apiRequest = new APIRequest();
        String sampleRequest = null;
        switch (teststep.getType()) {
            case Teststep.TYPE_HTTP:
                otherProperties = new HTTPTeststepProperties();
                break;
            case Teststep.TYPE_SOAP:
                otherProperties = new SOAPTeststepProperties();
                break;
            case Teststep.TYPE_DB:
                sampleRequest = "select * from ? where ?";
                break;
            case Teststep.TYPE_FTP:
                apiRequest = new FtpPutRequestFileFromText();
                break;
            case Teststep.TYPE_MQ:
                otherProperties = new MQTeststepProperties();
                break;
            case Teststep.TYPE_MQTT:
                apiRequest = new MQTTRequest();
                break;
            case Teststep.TYPE_WAIT:
                otherProperties = new WaitTeststepProperties("1000");
                break;
            default:
                break;
        }

        teststep.setOtherProperties(otherProperties);
        Endpoint endpoint = endpointDAO().createUnmanagedEndpoint(teststep.getType(), appMode);
        Object request = sampleRequest == null ? null : sampleRequest.getBytes();
        long id = _insertWithoutName(teststep, request, endpoint == null ? null : endpoint.getId(),
                new ObjectMapper().writeValueAsString(apiRequest));

        updateNameForInsert(id, "Step " + id);

        return id;
    }

    @Transaction
    default void insertByImport(Teststep teststep) throws JsonProcessingException {
        Long endpointId = null;
        if (teststep.getEndpoint() != null) {
            endpointId = endpointDAO().insertUnmanagedEndpoint(teststep.getEndpoint());
        }
        String requestString = (String) teststep.getRequest();
        byte[] request = null;
        if (requestString != null) {
            request = teststep.getRequestType() == TeststepRequestType.FILE ?
                    Base64.getDecoder().decode(requestString) : requestString.getBytes();
        }
        String apiRequestJSONString = new ObjectMapper().writeValueAsString(teststep.getApiRequest());
        long teststepId = _insertWithName(teststep, request, teststep.getRequestType().toString(), apiRequestJSONString,
                endpointId);

        for (Assertion assertion : teststep.getAssertions()) {
            assertion.setTeststepId(teststepId);
            assertionDAO().insert(assertion);
        }

        for (PropertyExtractor propertyExtractor: teststep.getPropertyExtractors()) {
            propertyExtractorDAO().insert(teststepId, propertyExtractor);
        }
    }

    @SqlUpdate("update teststep set name = :t.name, description = :t.description, action = :t.action, request = :request, " +
            "request_type = :requestType, request_filename = :t.requestFilename, api_request = :apiRequest, " +
            "endpoint_id = :endpointId, endpoint_property = :t.endpointProperty, other_properties = :t.otherProperties, " +
            "updated = CURRENT_TIMESTAMP where id = :t.id")
    void _updateWithStringRequest(@BindBean("t") Teststep teststep, @Bind("request") Object request,
                                  @Bind("requestType") String requestType,
                                  @Bind("apiRequest") String apiRequest,
                                  @Bind("endpointId") Long endpointId);

    @SqlUpdate("update teststep set name = :t.name, description = :t.description, request_type = :requestType, " +
            "request_filename = :t.requestFilename, action = :t.action, endpoint_id = :endpointId, " +
            "endpoint_property = :t.endpointProperty, other_properties = :t.otherProperties, updated = CURRENT_TIMESTAMP " +
            "where id = :t.id")
    void _updateWithoutRequest(@BindBean("t") Teststep teststep, @Bind("requestType") String requestType,
                               @Bind("endpointId") Long endpointId);

    @Transaction
    default void update(Teststep teststep) throws Exception {
        Teststep oldTeststep = findById_NoRequest(teststep.getId());  //  old request is not read into memory, to save memory

        switch (teststep.getType()) {
            case Teststep.TYPE_HTTP:
                processHTTPTeststepBackupRestore(oldTeststep, teststep);
                break;
            case Teststep.TYPE_DB:
                processDBTeststep(teststep);
                break;
            case Teststep.TYPE_JMS:
                processJMSTeststep(oldTeststep,teststep);
                break;
            case Teststep.TYPE_FTP:
                processFTPTeststep(oldTeststep, teststep);
                break;
            case Teststep.TYPE_MQ:
                processMQTeststep(oldTeststep, teststep);
                break;
            default:
                break;
        }

        Endpoint oldEndpoint = oldTeststep.getEndpoint();
        Endpoint newEndpoint = teststep.getEndpoint();
        Long newEndpointId = newEndpoint == null ? null : newEndpoint.getId();

        if (teststep.getRequestType() == TeststepRequestType.FILE) {    // update teststep without file request (this can save memory, as file could be big)
            _updateWithoutRequest(teststep, teststep.getRequestType().toString(), newEndpointId);
        } else {       // update teststep with string request
            Object request = teststep.getRequest() == null ? null : ((String) teststep.getRequest()).getBytes();
            String apiRequest = new ObjectMapper().writeValueAsString(teststep.getApiRequest());
            _updateWithStringRequest(teststep, request, teststep.getRequestType().toString(), apiRequest, newEndpointId);
        }

        updateEndpointIfExists(oldEndpoint, newEndpoint);

        updateAssertions(teststep);
    }

    default void processJMSTeststep(Teststep oldTeststep, Teststep teststep) {
        JMSTeststepProperties oldTeststepProperties = (JMSTeststepProperties) oldTeststep.getOtherProperties();
        JMSTeststepProperties newTeststepProperties = (JMSTeststepProperties) teststep.getOtherProperties();
        if (newTeststepProperties.getDestinationType() != oldTeststepProperties.getDestinationType()) {
            newTeststepProperties.setQueueName(null);
            newTeststepProperties.setTopicString(null);
        }

        if (teststep.getAction() != null) {      //  newly created JMS test step does not have action and does not need the processing
            String oldAction = oldTeststep.getAction();
            String newAction = teststep.getAction();
            if (!newAction.equals(oldAction)) {    //  action is switched, so clear things
                teststep.setApiRequest(null);
                teststep.getAssertions().clear();

                switch (newAction) {
                    case Teststep.ACTION_CHECK_DEPTH:
                        Assertion assertion = new Assertion();
                        teststep.getAssertions().add(assertion);
                        assertion.setName("Queue depth equals");
                        assertion.setType(Assertion.TYPE_INTEGER_EQUAL);
                        assertion.setOtherProperties(new IntegerEqualAssertionProperties(0));
                        break;
                    case Teststep.ACTION_SEND:
                        //  fall through
                    case Teststep.ACTION_PUBLISH:
                        teststep.setApiRequest(new JMSRequest());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    default void processHTTPTeststepBackupRestore(Teststep oldTeststep, Teststep teststep) {
        HTTPMethod oldHTTPMethod = ((HTTPTeststepProperties) oldTeststep.getOtherProperties()).getHttpMethod();
        HTTPMethod newHTTPMethod = ((HTTPTeststepProperties) teststep.getOtherProperties()).getHttpMethod();
        if ((oldHTTPMethod == HTTPMethod.POST || oldHTTPMethod == HTTPMethod.PUT) &&
                (newHTTPMethod == HTTPMethod.GET || newHTTPMethod == HTTPMethod.DELETE)) {  // backup and then clear request
            saveStepDataBackupById(teststep.getId(), (String) teststep.getRequest());
            teststep.setRequest(null);
        } else if ((oldHTTPMethod == HTTPMethod.GET || oldHTTPMethod == HTTPMethod.DELETE) &&
                (newHTTPMethod == HTTPMethod.POST || newHTTPMethod == HTTPMethod.PUT)) {  // restore request and then clear backup which is no longer useful
            teststep.setRequest(getStepDataBackupById(teststep.getId()));
            saveStepDataBackupById(teststep.getId(), null);
        }
    }

    default void processFTPTeststep(Teststep oldTeststep, Teststep teststep) {
        FtpPutFileFrom oldFileFrom = ((FtpPutRequest) oldTeststep.getApiRequest()).getFileFrom();
        FtpPutRequest ftpPutRequest = (FtpPutRequest) teststep.getApiRequest();
        FtpPutFileFrom fileFrom = ftpPutRequest.getFileFrom();
        if (fileFrom != oldFileFrom) {         //  switching between file from text/file
            teststep.setApiRequest(fileFrom == FtpPutFileFrom.TEXT ?
                    new FtpPutRequestFileFromText(ftpPutRequest) :
                    new FtpPutRequestFileFromFile(ftpPutRequest));
        } else if (fileFrom == FtpPutFileFrom.FILE) {
            FtpPutRequestFileFromFile oldFtpPutRequestFileFromFile = (FtpPutRequestFileFromFile) oldTeststep.getApiRequest();
            FtpPutRequestFileFromFile ftpPutRequestFileFromFile = (FtpPutRequestFileFromFile) ftpPutRequest;
            ftpPutRequestFileFromFile.setFileContent(oldFtpPutRequestFileFromFile.getFileContent());
        }
    }

    default void processDBTeststep(Teststep teststep) {
        if (!IronTestUtils.isSQLRequestSingleSelectStatement((String) teststep.getRequest())) {
            teststep.getAssertions().clear();
        }
    }

    default void processMQTeststep(Teststep oldTeststep, Teststep teststep) throws IOException {
        if (teststep.getAction() != null) {      //  newly created MQ test step does not have action and does not need the processing
            String newAction = teststep.getAction();
            if (!newAction.equals(oldTeststep.getAction()) ||
                    teststep.getRequestType() != oldTeststep.getRequestType()) {    //  action or 'message from' is switched, so clear things
                teststep.setRequest(null);
                teststep.setRequestFilename(null);
                teststep.getAssertions().clear();
                MQTeststepProperties properties = (MQTeststepProperties) teststep.getOtherProperties();
                properties.setRfh2Header(null);

                backupRestoreMQTeststepActionData(oldTeststep, teststep);
            } else if (TeststepRequestType.TEXT == teststep.getRequestType() && (
                    Teststep.ACTION_ENQUEUE.equals(newAction) || Teststep.ACTION_PUBLISH.equals(newAction))) {
                processMQTeststepRFH2Folders(teststep);
            }
        }
    }

    default void processMQTeststepRFH2Folders(Teststep teststep) {
        MQTeststepProperties mqTeststepProperties = (MQTeststepProperties) teststep.getOtherProperties();
        MQRFH2Header rfh2Header = mqTeststepProperties.getRfh2Header();
        if (rfh2Header != null) {
            List<MQRFH2Folder> rfh2Folders = rfh2Header.getFolders();
            for (MQRFH2Folder folder : rfh2Folders) {
                IronTestUtils.validateMQRFH2FolderStringAndSetFolderName(folder);
            }
        }
    }

    default void backupRestoreMQTeststepActionData(Teststep oldTeststep, Teststep teststep) throws IOException {
        long teststepId = teststep.getId();
        String oldAction = oldTeststep.getAction();
        String newAction = teststep.getAction();
        String backupStr = getStepDataBackupById(teststepId);
        MQTeststepActionDataBackup newBackup = new MQTeststepActionDataBackup();
        MQTeststepActionDataBackup oldBackup = null;
        if (backupStr != null) {
            ObjectMapper mapper = new ObjectMapper();
            oldBackup = mapper.readValue(backupStr, MQTeststepActionDataBackup.class);
        }
        boolean persistNewBackup = false;

        // backup old action's data
        // for assertions, no need to backup primary keys or foreign keys
        List<Assertion> oldAssertions = oldTeststep.getAssertions();
        for (Assertion oldAssertion: oldAssertions) {
            oldAssertion.setId(null);
            oldAssertion.setTeststepId(null);
        }
        if (Teststep.ACTION_CHECK_DEPTH.equals(oldAction)) {
            Assertion oldAssertion = oldAssertions.get(0);
            newBackup.setQueueDepthAssertionProperties(
                    (IntegerEqualAssertionProperties) oldAssertion.getOtherProperties());
            persistNewBackup = true;
        } else if (Teststep.ACTION_DEQUEUE.equals(oldAction)) {
            newBackup.setDequeueAssertions(oldAssertions);
            persistNewBackup = true;
        } else if (Teststep.ACTION_ENQUEUE.equals(oldAction) || Teststep.ACTION_PUBLISH.equals(oldAction)) {
            byte[] oldRequest = (byte[]) getBinaryRequestById(teststepId);
            newBackup.setRequest(oldRequest);
            if (TeststepRequestType.TEXT == oldTeststep.getRequestType()) {
                newBackup.setRfh2Header(((MQTeststepProperties) oldTeststep.getOtherProperties()).getRfh2Header());
            } else if (TeststepRequestType.FILE == oldTeststep.getRequestType()) {
                newBackup.setRequestFilename(oldTeststep.getRequestFilename());
            }
            persistNewBackup = true;
        }

        if (persistNewBackup) {
            backupStr = new ObjectMapper().writeValueAsString(newBackup);
            saveStepDataBackupById(teststepId, backupStr);
        }

        // setup new action's data
        if (Teststep.ACTION_CHECK_DEPTH.equals(newAction)) {
            Assertion assertion = new Assertion();
            teststep.getAssertions().add(assertion);
            assertion.setName("MQ queue depth equals");
            assertion.setType(Assertion.TYPE_INTEGER_EQUAL);
            // restore old assertion properties if exists
            IntegerEqualAssertionProperties oldAssertionProperties =
                    oldBackup == null ? null : oldBackup.getQueueDepthAssertionProperties();
            if (oldAssertionProperties != null) {
                assertion.setOtherProperties(oldAssertionProperties);
            } else {
                assertion.setOtherProperties(new IntegerEqualAssertionProperties(0));
            }
        } else if (Teststep.ACTION_DEQUEUE.equals(newAction) && oldBackup != null) {
            // restore old assertions if exist
            if (oldBackup.getDequeueAssertions() != null) {
                teststep.getAssertions().addAll(oldBackup.getDequeueAssertions());
            }
        } else if ((Teststep.ACTION_ENQUEUE.equals(newAction) || Teststep.ACTION_PUBLISH.equals(newAction)) && oldBackup != null) {
            if (TeststepRequestType.TEXT == teststep.getRequestType()) {
                // restore old request
                teststep.setRequest(oldBackup.getRequest() == null ? null : new String(oldBackup.getRequest()));
                ((MQTeststepProperties) teststep.getOtherProperties()).setRfh2Header(oldBackup.getRfh2Header());
            } else if (TeststepRequestType.FILE == teststep.getRequestType()) {
                // restore old request
                updateRequest(teststep.getId(), oldBackup.getRequest());
                teststep.setRequestFilename(oldBackup.getRequestFilename());
            }
        }
    }

    @SqlUpdate("update teststep set step_data_backup = :backupData, updated = CURRENT_TIMESTAMP " +
            "where id = :teststepId")
    void saveStepDataBackupById(@Bind("teststepId") long teststepId,
                                @Bind("backupData") String backupData);

    @SqlQuery("select step_data_backup from teststep where id = :teststepId")
    String getStepDataBackupById(@Bind("teststepId") long teststepId);

    @Transaction
    default void updateAssertions(Teststep teststep) {
        AssertionDAO assertionDAO = assertionDAO();
        List<Long> newAssertionIds = new ArrayList<>();
        for (Assertion assertion: teststep.getAssertions()) {
            if (assertion.getId() == null) {    //  insert the assertion
                assertion.setTeststepId(teststep.getId());
                newAssertionIds.add(assertionDAO.insert(assertion));
            } else {                            //  update the assertion
                newAssertionIds.add(assertion.getId());
                assertionDAO.update(assertion);
            }
        }
        //  delete assertions whose id is not in the newAssertionIds list;
        //  if newAssertionIds list is empty, delete all assertions
        newAssertionIds.add(-1L);
        assertionDAO.deleteByTeststepIdIfIdNotIn(teststep.getId(), newAssertionIds);
    }

    default void updateEndpointIfExists(Endpoint oldEndpoint, Endpoint newEndpoint) {
        if (newEndpoint != null) {
            if (newEndpoint.isManaged()) {
                if (oldEndpoint.isManaged()) {
                    //  do nothing
                } else {
                    if (newEndpoint.getId() == oldEndpoint.getId()) {
                        //  the old unmanaged endpoint is shared by user and becomes managed, so save it
                        endpointDAO().update(newEndpoint);
                    } else {
                        //  the old unmanaged endpoint is replaced by an existing managed endpoint, so delete the old one
                        endpointDAO().deleteById(oldEndpoint.getId());
                    }
                }
            } else {  //  new endpoint is still unmanaged, so update it
                endpointDAO().update(newEndpoint);
            }
        }
    }

    @SqlUpdate("delete from teststep where id = :id")
    void _deleteById(@Bind("id") long id);

    @Transaction
    default void deleteById(long id) {
        Teststep teststep = findById_NoRequest(id);
        _deleteById(id);
        // decrement sequence number of all next test steps
        batchMove(teststep.getTestcaseId(), (short) (teststep.getSequence() + 1), Short.MAX_VALUE, STEP_MOVE_DIRECTION_UP);

        Endpoint endpoint = teststep.getEndpoint();
        if (endpoint != null && !endpoint.isManaged()) {  //  delete the teststep's endpoint if it exists and is unmanaged
            endpointDAO().deleteById(endpoint.getId());
        }
    }

    @SqlQuery("select id, testcase_id, sequence, name, type, description, action, endpoint_id, endpoint_property, " +
            "request_type, request_filename, api_request, other_properties, step_data_backup " +
            "from teststep where id = :id")
    Teststep _findById_NoRequest(@Bind("id") long id);

    @SqlQuery("select * from teststep where id = :id")
    Teststep _findById_Complete(@Bind("id") long id);

    @SqlQuery("select testcase_id from teststep where id = :id")
    long findTestcaseIdById(@Bind("id") long id);

    @Transaction
    default void populateTeststepWithOtherDetails(Teststep teststep) {
        Endpoint endpoint = endpointDAO().findById(teststep.getEndpoint().getId());
        teststep.setEndpoint(endpoint);
        teststep.setAssertions(assertionDAO().findByTeststepId(teststep.getId()));
        teststep.setPropertyExtractors(propertyExtractorDAO().findByTeststepId(teststep.getId()));
    }

    @Transaction
    default Teststep findById_NoRequest(long id) {
        Teststep teststep = _findById_NoRequest(id);
        if (teststep != null) {
            populateTeststepWithOtherDetails(teststep);
        }
        return teststep;
    }


    @Transaction
    default Teststep findById_Complete(long id) {
        Teststep teststep = _findById_Complete(id);
        if (teststep != null) {
            populateTeststepWithOtherDetails(teststep);
        }
        return teststep;
    }

    @SqlQuery("select * from teststep where testcase_id = :testcaseId order by sequence")
    List<Teststep> _findByTestcaseId_Complete(@Bind("testcaseId") long testcaseId);

    default List<Teststep> findByTestcaseId_Complete(long testcaseId) {
        List<Teststep> teststeps = _findByTestcaseId_Complete(testcaseId);
        for (Teststep teststep: teststeps) {
            populateTeststepWithOtherDetails(teststep);
        }
        return teststeps;
    }

    @SqlQuery("select id, testcase_id, sequence, name, type, description from teststep " +
              "where testcase_id = :testcaseId order by sequence")
    List<Teststep> findByTestcaseId_TestcaseEditView(@Bind("testcaseId") long testcaseId);

    @SqlQuery("select id from teststep where testcase_id = :testcaseId and sequence = :sequence")
    long findIdBySequence(@Bind("testcaseId") long testcaseId, @Bind("sequence") short sequence);

    @SqlQuery("select id from teststep where testcase_id = :testcaseId")
    List<Long> findIdsByTestcaseId(@Bind("testcaseId") long testcaseId);

    @SqlQuery("select request_type from teststep where id = :teststepId")
    TeststepRequestType findRequestTypeById(@Bind("teststepId") long teststepId);

    @SqlUpdate("update teststep set sequence = :newSequence, updated = CURRENT_TIMESTAMP where id = :teststepId")
    void updateSequenceById(@Bind("teststepId") long teststepId, @Bind("newSequence") short newSequence);

    @SqlUpdate("update teststep set endpoint_id = :endpointId where id = :teststepId")
    void updateEndpointIdByIdForDuplication(@Bind("teststepId") long teststepId, @Bind("endpointId") long endpointId);

    @SqlUpdate("update teststep set sequence = case when :direction = '" + STEP_MOVE_DIRECTION_UP + "' then sequence - 1 else sequence + 1 end, " +
            "updated = CURRENT_TIMESTAMP " +
            "where testcase_id = :testcaseId and sequence >= :firstSequence and sequence <= :lastSequence")
    void batchMove(@Bind("testcaseId") long testcaseId,
                   @Bind("firstSequence") short firstSequence,
                   @Bind("lastSequence") short lastSequence,
                   @Bind("direction") String direction);

    @Transaction
    default void moveInTestcase(long testcaseId, short fromSequence, short toSequence) {
        if (fromSequence != toSequence) {
            long draggedStepId = findIdBySequence(testcaseId, fromSequence);

            //  shelve the dragged step first
            updateSequenceById(draggedStepId, (short) -1);

            if (fromSequence < toSequence) {
                batchMove(testcaseId, (short) (fromSequence + 1), toSequence, STEP_MOVE_DIRECTION_UP);
            } else {
                batchMove(testcaseId, toSequence, (short) (fromSequence - 1), STEP_MOVE_DIRECTION_DOWN);
            }

            //  move the dragged step last
            updateSequenceById(draggedStepId, toSequence);
        }
    }

    @SqlUpdate("update teststep set request = :request, updated = CURRENT_TIMESTAMP where id = :teststepId")
    void updateRequest(@Bind("teststepId") long teststepId, @Bind("request") byte[] request);

    @SqlUpdate("update teststep set request = :request, request_type = :requestType, request_filename = :requestFilename, " +
            "updated = CURRENT_TIMESTAMP where id = :teststepId")
    void _setRequestFile(@Bind("teststepId") long teststepId,
                         @Bind("request") byte[] request,   //  InputStream used to work here with jdbi v2, but it is not working with jdbi v3
                         @Bind("requestType") String requestType,
                         @Bind("requestFilename") String requestFilename);

    @Transaction
    default Teststep setRequestFile(long teststepId, String fileName, InputStream inputStream) throws IOException {
        byte[] fileBytes;
        try {
            fileBytes = IOUtils.toByteArray(inputStream);
        } finally {
            inputStream.close();
        }
        _setRequestFile(teststepId, fileBytes, TeststepRequestType.FILE.toString(), fileName);

        return findById_NoRequest(teststepId);
    }

    //  byte[] return type used to work in jdbi v2, but it is not working in jdbi v3
    //  in jdbi v3, ColumnMapper<byte[]> is not working here
    @SqlQuery("select request from teststep where id = :teststepId")
    @RegisterColumnMapper(ObjectColumnMapper.class)
    Object getBinaryRequestById(@Bind("teststepId") long teststepId);

    @SqlQuery("select api_request from teststep where id = :teststepId")
    @RegisterColumnMapper(APIRequestColumMapper.class)
    APIRequest getAPIRequestById(@Bind("teststepId") long teststepId);

    @SqlUpdate("update teststep set endpoint_id = null, endpoint_property = 'Endpoint', " +
            "updated = CURRENT_TIMESTAMP where id = :teststepId")
    void switchToEndpointProperty(@Bind("teststepId") long teststepId);

    @SqlUpdate("update teststep set endpoint_id = :endpointId, endpoint_property = null, " +
            "updated = CURRENT_TIMESTAMP where id = :teststepId")
    void switchToDirectEndpoint(@Bind("teststepId") long teststepId,
                                @Bind("endpointId") long endpointId);

    @Transaction
    default void useEndpointProperty(Teststep teststep) {
        switchToEndpointProperty(teststep.getId());
        endpointDAO().deleteUnmanagedEndpointById(teststep.getEndpoint().getId());
    }

    @Transaction
    default void useDirectEndpoint(Teststep teststep, AppMode appMode) throws JsonProcessingException {
        Endpoint endpoint = endpointDAO().createUnmanagedEndpoint(teststep.getType(), appMode);
        switchToDirectEndpoint(teststep.getId(), endpoint.getId());
    }

    @SqlUpdate("insert into teststep (testcase_id, sequence, name, type, description, action, request, request_type, " +
            "request_filename, api_request, endpoint_id, endpoint_property, other_properties) select :newTestcaseId, " +
            "sequence, name, type, description, action, request, request_type, request_filename, api_request, " +
            "endpoint_id, endpoint_property, other_properties from teststep where id = :oldTeststepId")
    @GetGeneratedKeys
    long duplicateById(@Bind("oldTeststepId") long oldTeststepId, @Bind("newTestcaseId") long newTestcaseId);

    @Transaction
    default void duplicateByTestcase(long sourceTestcaseId, long newTestcaseId) {
        List<Long> oldTeststepIds = findIdsByTestcaseId(sourceTestcaseId);

        for (long oldTeststepId : oldTeststepIds) {
            long newTeststepId = duplicateById(oldTeststepId, newTestcaseId);

            //  duplicate endpoint if needed
            Long newEndpointId = endpointDAO().duplicateUnmanagedEndpoint(oldTeststepId);
            if (newEndpointId != null) {
                updateEndpointIdByIdForDuplication(newTeststepId, newEndpointId);
            }

            //  duplicate assertions
            assertionDAO().duplicateByTeststep(oldTeststepId, newTeststepId);

            //  duplicate property extractors
            propertyExtractorDAO().duplicateByTeststep(oldTeststepId, newTeststepId);
        }
    }

    @SqlUpdate("update teststep set api_request = :apiRequest, updated = CURRENT_TIMESTAMP where id = :teststepId")
    void saveApiRequest(@Bind("teststepId") long teststepId, @Bind("apiRequest") String apiRequest);

    @Transaction
    default Teststep saveApiRequestFile(long teststepId, String fileName, InputStream inputStream) throws IOException {
        Teststep teststep = findById_Complete(teststepId);
        if (Teststep.TYPE_FTP.equals(teststep.getType())) {
            FtpPutRequestFileFromFile putRequest = (FtpPutRequestFileFromFile) teststep.getApiRequest();
            putRequest.setFileName(fileName);
            byte[] fileBytes;
            try {
                fileBytes = IOUtils.toByteArray(inputStream);
            } finally {
                inputStream.close();
            }
            putRequest.setFileContent(fileBytes);

            saveApiRequest(teststepId, new ObjectMapper().writeValueAsString(putRequest));
        }

        return findById_Complete(teststepId);
    }

    @Transaction
    default void unmanageEndpoint(long teststepId) {
        long newEndpointId = endpointDAO().duplicateManagedEndpointIntoUnmanaged(teststepId);
        updateEndpointIdByIdForDuplication(teststepId, newEndpointId);
    }
}
