package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.core.MQTeststepActionDataBackup;
import io.irontest.models.AppMode;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.IntegerEqualAssertionProperties;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.*;
import io.irontest.utils.XMLUtils;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

/**
 * Created by Zheng on 7/07/2015.
 */
@RegisterMapper(TeststepMapper.class)
public abstract class TeststepDAO {
    private static final String STEP_MOVE_DIRECTION_UP = "up";
    private static final String STEP_MOVE_DIRECTION_DOWN = "down";

    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS teststep_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS teststep (" +
            "id BIGINT DEFAULT teststep_sequence.NEXTVAL PRIMARY KEY, testcase_id BIGINT NOT NULL, " +
            "sequence SMALLINT NOT NULL, name VARCHAR(200) NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "type VARCHAR(20) NOT NULL, description CLOB, action VARCHAR(50), endpoint_id BIGINT, " +
            "endpoint_property VARCHAR(200), request BLOB, request_type VARCHAR(20) NOT NULL DEFAULT 'Text', " +
            "request_filename VARCHAR(200), other_properties CLOB, action_data_backup CLOB, " +
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
    public abstract void createTableIfNotExists();

    @CreateSqlObject
    protected abstract EndpointDAO endpointDAO();

    @CreateSqlObject
    protected abstract AssertionDAO assertionDAO();

    /**
     * This method considers test step insertion from both Create (test step) button on UI and test case duplicating.
     * @param teststep
     * @param request
     * @param requestType
     * @param endpointId
     * @param otherProperties
     * @return
     */
    @SqlUpdate("insert into teststep (testcase_id, sequence, type, description, action, request, request_type, " +
            "request_filename, endpoint_id, other_properties) values (:t.testcaseId, " +
                "case when :t.sequence = 0 " +
                    "then (select coalesce(max(sequence), 0) + 1 from teststep where testcase_id = :t.testcaseId) " +
                    "else :t.sequence end, " +
            ":t.type, :t.description, :t.action, :request, :requestType, :t.requestFilename, :endpointId, " +
            ":otherProperties)")
    @GetGeneratedKeys
    protected abstract long _insert(@BindBean("t") Teststep teststep, @Bind("request") Object request,
                                    @Bind("requestType") String requestType, @Bind("endpointId") Long endpointId,
                                    @Bind("otherProperties") String otherProperties);

    @SqlUpdate("update teststep set name = :name where id = :id")
    protected abstract long updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @Transaction
    public long insert(Teststep teststep, AppMode appMode) throws JsonProcessingException {
        return insert_NoTransaction(teststep, appMode);
    }

    /**
     * This method considers test step insertion from both Create (test step) button on UI and test case duplicating.
     * @param teststep
     * @param appMode
     * @return
     * @throws JsonProcessingException
     */
    public long insert_NoTransaction(Teststep teststep, AppMode appMode) throws JsonProcessingException {
        Endpoint endpoint = teststep.getEndpoint();
        if (endpoint == null) {    //  from test step creation on UI
            endpoint = endpointDAO().createUnmanagedEndpoint_NoTransaction(teststep.getType(), appMode);
        } else if (endpoint.getId() == 0){          //  from test case duplicating
            long endpointId = endpointDAO().insertUnmanagedEndpoint_NoTransaction(endpoint);
            endpoint.setId(endpointId);
        }
        Object request = teststep.getRequest() instanceof String ?
                ((String) teststep.getRequest()).getBytes() : teststep.getRequest();
        String otherProperties = new ObjectMapper().writeValueAsString(teststep.getOtherProperties());
        long id = _insert(teststep, request, teststep.getRequestType().toString(),
                endpoint == null ? null : endpoint.getId(), otherProperties);

        if (teststep.getName() == null) {    //  from test step creation on UI
            teststep.setName("Step " + id);
        }
        updateNameForInsert(id, teststep.getName());

        return id;
    }

    @SqlUpdate("update teststep set name = :name, description = :description, action = :action, request = :request, " +
            "request_type = :requestType, endpoint_id = :endpointId, endpoint_property = :endpointProperty, " +
            "other_properties = :otherProperties, updated = CURRENT_TIMESTAMP where id = :id")
    protected abstract int _update(@Bind("name") String name, @Bind("description") String description,
                                   @Bind("action") String action, @Bind("request") Object request,
                                   @Bind("requestType") String requestType, @Bind("id") long id,
                                   @Bind("endpointId") Long endpointId, @Bind("endpointProperty") String endpointProperty,
                                   @Bind("otherProperties") String otherProperties);

    @SqlUpdate("update teststep set name = :name, description = :description, request_type = :requestType, " +
            "action = :action, endpoint_id = :endpointId, endpoint_property = :endpointProperty, " +
            "other_properties = :otherProperties, updated = CURRENT_TIMESTAMP where id = :id")
    protected abstract int _updateWithoutRequest(@Bind("name") String name, @Bind("description") String description,
                                                 @Bind("requestType") String requestType,
                                                 @Bind("action") String action, @Bind("id") long id,
                                                 @Bind("endpointId") Long endpointId,
                                                 @Bind("endpointProperty") String endpointProperty,
                                                 @Bind("otherProperties") String otherProperties);

    @Transaction
    public Teststep update(Teststep teststep) throws Exception {
        Teststep oldTeststep = findById_NoTransaction(teststep.getId());

        if (Teststep.TYPE_MQ.equals(teststep.getType()) && teststep.getAction() != null) {   //  newly created MQ test step does not have action and does not need the processing
            processMQTeststep(oldTeststep, teststep);
        }

        Endpoint oldEndpoint = oldTeststep.getEndpoint();
        Endpoint newEndpoint = teststep.getEndpoint();
        Long newEndpointId = newEndpoint == null ? null : newEndpoint.getId();
        String otherProperties = new ObjectMapper().writeValueAsString(teststep.getOtherProperties());

        if (teststep.getRequestType() == TeststepRequestType.FILE) {    // update teststep without request
            _updateWithoutRequest(teststep.getName(), teststep.getDescription(), teststep.getRequestType().toString(),
                    teststep.getAction(), teststep.getId(), newEndpointId, teststep.getEndpointProperty(), otherProperties);
        } else {       // update teststep with request
            Object request = teststep.getRequest() instanceof String ?
                    ((String) teststep.getRequest()).getBytes() : teststep.getRequest();
            _update(teststep.getName(), teststep.getDescription(), teststep.getAction(), request,
                    teststep.getRequestType().toString(), teststep.getId(), newEndpointId,
                    teststep.getEndpointProperty(), otherProperties);
        }

        updateEndpointIfExists(oldEndpoint, newEndpoint);

        updateAssertions(teststep);

        return findById_NoTransaction(teststep.getId());
    }

    private void processMQTeststep(Teststep oldTeststep, Teststep teststep) throws IOException {
        String newAction = teststep.getAction();
        if (!newAction.equals(oldTeststep.getAction()) ||
                teststep.getRequestType() != oldTeststep.getRequestType()) {    //  action or 'message from' is switched, so clear things
            teststep.setRequest(null);
            teststep.setRequestFilename(null);
            teststep.getAssertions().clear();
            MQTeststepProperties properties = (MQTeststepProperties) teststep.getOtherProperties();
            properties.setRfh2Header(new MQRFH2Header());

            backupRestoreMQTeststepActionData(oldTeststep, teststep);
        } else if (TeststepRequestType.TEXT == teststep.getRequestType() && (
                Teststep.ACTION_ENQUEUE.equals(newAction) || Teststep.ACTION_PUBLISH.equals(newAction))) {
            processMQTeststepRFH2Folders(teststep);
        }
    }

    private void processMQTeststepRFH2Folders(Teststep teststep) {
        MQTeststepProperties mqTeststepProperties = (MQTeststepProperties) teststep.getOtherProperties();
        MQRFH2Header rfh2Header = mqTeststepProperties.getRfh2Header();
        if (rfh2Header.isEnabled()) {
            List<MQRFH2Folder> rfh2Folders = rfh2Header.getFolders();
            for (MQRFH2Folder folder : rfh2Folders) {
                //  validate folder string is well formed XML
                Document doc = null;
                try {
                    doc = XMLUtils.xmlStringToDOM(folder.getString());
                } catch (Exception e) {
                    throw new RuntimeException("Folder string is not a valid XML. " + folder.getString(), e);
                }

                //  update folder name to be the XML root element name
                folder.setName(doc.getDocumentElement().getTagName());
            }
        }
    }

    private void backupRestoreMQTeststepActionData(Teststep oldTeststep, Teststep teststep) throws IOException {
        long teststepId = teststep.getId();
        String oldAction = oldTeststep.getAction();
        String newAction = teststep.getAction();
        String backupStr = getActionDataBackupById(teststepId);
        MQTeststepActionDataBackup backup = null;
        MQTeststepActionDataBackup oldBackup = null;
        if (backupStr == null) {
            backup = new MQTeststepActionDataBackup();
            oldBackup = new MQTeststepActionDataBackup();
        } else {
            ObjectMapper mapper = new ObjectMapper();
            backup = mapper.readValue(backupStr, MQTeststepActionDataBackup.class);
            oldBackup = mapper.readValue(backupStr, MQTeststepActionDataBackup.class);
        }
        boolean backupChanged = false;

        // backup old action's data
        // for assertions, no need to backup primary keys or foreign keys
        List<Assertion> oldAssertions = oldTeststep.getAssertions();
        for (Assertion oldAssertion: oldAssertions) {
            oldAssertion.setId(null);
            oldAssertion.setTeststepId(null);
        }
        if (Teststep.ACTION_CHECK_DEPTH.equals(oldAction)) {
            Assertion oldAssertion = oldAssertions.get(0);
            backup.setQueueDepthAssertionProperties(
                    (IntegerEqualAssertionProperties) oldAssertion.getOtherProperties());
            backupChanged = true;
        } else if (Teststep.ACTION_DEQUEUE.equals(oldAction)) {
            backup.setDequeueAssertions(oldAssertions);
            backupChanged = true;
        } else if (Teststep.ACTION_ENQUEUE.equals(oldAction) || Teststep.ACTION_PUBLISH.equals(oldAction)) {
            if (TeststepRequestType.TEXT == oldTeststep.getRequestType()) {
                backup.setTextRequest((String) oldTeststep.getRequest());
                backup.setRfh2Header(((MQTeststepProperties) oldTeststep.getOtherProperties()).getRfh2Header());
                backupChanged = true;
            } else if (TeststepRequestType.FILE == oldTeststep.getRequestType()) {
                backup.setFileRequest(getBinaryRequestById(teststepId));
                backup.setRequestFilename(oldTeststep.getRequestFilename());
                backupChanged = true;
            }
        }

        //  persist backup if changed
        if (backupChanged) {
            backupStr = new ObjectMapper().writeValueAsString(backup);
            saveActionDataBackupById(teststepId, backupStr);
        }

        // setup new action's data
        if (Teststep.ACTION_CHECK_DEPTH.equals(newAction)) {
            Assertion assertion = new Assertion();
            teststep.getAssertions().add(assertion);
            assertion.setName("MQ queue depth equals");
            assertion.setType(Assertion.TYPE_INTEGER_EQUAL);
            // restore old assertion properties if exists
            IntegerEqualAssertionProperties oldAssertionProperties =
                    oldBackup.getQueueDepthAssertionProperties();
            if (oldAssertionProperties != null) {
                assertion.setOtherProperties(oldAssertionProperties);
            } else {
                assertion.setOtherProperties(new IntegerEqualAssertionProperties(0));
            }
        } else if (Teststep.ACTION_DEQUEUE.equals(newAction)) {
            // restore old assertions if exist
            if (oldBackup.getDequeueAssertions() != null) {
                teststep.getAssertions().addAll(oldBackup.getDequeueAssertions());
            }
        } else if (Teststep.ACTION_ENQUEUE.equals(newAction) || Teststep.ACTION_PUBLISH.equals(newAction)) {
            if (TeststepRequestType.TEXT == teststep.getRequestType()) {
                // restore old message
                teststep.setRequest(oldBackup.getTextRequest());
                //  teststep.otherProperties.rfh2Header should never be null
                ((MQTeststepProperties) teststep.getOtherProperties()).setRfh2Header(
                        oldBackup.getRfh2Header() == null ? new MQRFH2Header() : oldBackup.getRfh2Header());
            } else if (TeststepRequestType.FILE == teststep.getRequestType()) {
                // restore old message
                updateRequest(teststep.getId(), oldBackup.getFileRequest(), TeststepRequestType.FILE.toString(),
                        oldBackup.getRequestFilename());
            }
        }
    }

    @SqlUpdate("update teststep set action_data_backup = :backupJSON, updated = CURRENT_TIMESTAMP " +
            "where id = :teststepId")
    protected abstract int saveActionDataBackupById(@Bind("teststepId") long teststepId,
                                                    @Bind("backupJSON") String backupJSON);

    @SqlQuery("select action_data_backup from teststep where id = :teststepId")
    protected abstract String getActionDataBackupById(@Bind("teststepId") long teststepId);

    private void updateAssertions(Teststep teststep) throws JsonProcessingException {
        AssertionDAO assertionDAO = assertionDAO();
        List<Long> newAssertionIds = new ArrayList<Long>();
        for (Assertion assertion: teststep.getAssertions()) {
            if (assertion.getId() == null) {    //  insert the assertion
                assertion.setTeststepId(teststep.getId());
                newAssertionIds.add(assertionDAO.insert_NoTransaction(assertion));
            } else {                            //  update the assertion
                newAssertionIds.add(assertion.getId());
                assertionDAO.update_NoTransaction(assertion);
            }
        }
        //  delete assertions whose id is not in the newAssertionIds list;
        //  if newAssertionIds list is empty, delete all assertions
        newAssertionIds.add(-1L);
        assertionDAO.deleteByTeststepIdIfIdNotIn(teststep.getId(), newAssertionIds);
    }

    private void updateEndpointIfExists(Endpoint oldEndpoint, Endpoint newEndpoint) throws JsonProcessingException {
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
    protected abstract void _deleteById(@Bind("id") long id);

    @Transaction
    public void deleteById(long id) {
        deleteById_NoTransaction(id);
    }

    protected void deleteById_NoTransaction(long id) {
        Teststep teststep = findById_NoTransaction(id);
        _deleteById(id);
        // decrement sequence number of all next test steps
        batchMove(teststep.getTestcaseId(), (short) (teststep.getSequence() + 1), Short.MAX_VALUE, STEP_MOVE_DIRECTION_UP);

        Endpoint endpoint = teststep.getEndpoint();
        if (endpoint != null && !endpoint.isManaged()) {  //  delete the teststep's endpoint if it exists and is unmanaged
            endpointDAO().deleteById(endpoint.getId());
        }
    }

    @SqlQuery("select * from teststep where id = :id")
    protected abstract Teststep _findById(@Bind("id") long id);

    @SqlQuery("select testcase_id from teststep where id = :id")
    public abstract long findTestcaseIdById(@Bind("id") long id);

    /**
     * @param id
     * @return the teststep with its associated endpoint
     */
    @Transaction
    public Teststep findById(long id) {
        return findById_NoTransaction(id);
    }

    private void populateTeststepWithMoreInfo(Teststep teststep) {
        Endpoint endpoint = endpointDAO().findById(teststep.getEndpoint().getId());
        teststep.setEndpoint(endpoint);
        teststep.setAssertions(assertionDAO().findByTeststepId(teststep.getId()));
    }

    private Teststep findById_NoTransaction(long id) {
        Teststep teststep = _findById(id);
        if (teststep != null) {
            populateTeststepWithMoreInfo(teststep);
        }
        return teststep;
    }

    @SqlQuery("select * from teststep where testcase_id = :testcaseId order by sequence")
    protected abstract List<Teststep> _findByTestcaseId(@Bind("testcaseId") long testcaseId);

    protected List<Teststep> findByTestcaseId(long testcaseId) {
        List<Teststep> teststeps = _findByTestcaseId(testcaseId);
        for (Teststep teststep: teststeps) {
            populateTeststepWithMoreInfo(teststep);
        }
        return teststeps;
    }

    @SqlQuery("select id, testcase_id, sequence, name, type, description from teststep " +
              "where testcase_id = :testcaseId order by sequence")
    protected abstract List<Teststep> findByTestcaseId_TestcaseEditView(@Bind("testcaseId") long testcaseId);

    @SqlQuery("select * from teststep where testcase_id = :testcaseId and sequence = :sequence")
    protected abstract Teststep findBySequence(@Bind("testcaseId") long testcaseId, @Bind("sequence") short sequence);

    @SqlUpdate("update teststep set sequence = :newSequence, updated = CURRENT_TIMESTAMP where id = :teststepId")
    protected abstract int updateSequenceById(@Bind("teststepId") long teststepId, @Bind("newSequence") short newSequence);

    @SqlUpdate("update teststep set sequence = case when :direction = '" + STEP_MOVE_DIRECTION_UP + "' then sequence - 1 else sequence + 1 end, " +
            "updated = CURRENT_TIMESTAMP " +
            "where testcase_id = :testcaseId and sequence >= :firstSequence and sequence <= :lastSequence")
    protected abstract int batchMove(@Bind("testcaseId") long testcaseId,
                                              @Bind("firstSequence") short firstSequence,
                                              @Bind("lastSequence") short lastSequence,
                                              @Bind("direction") String direction);

    @Transaction
    public void moveInTestcase(long testcaseId, short fromSequence, short toSequence) {
        if (fromSequence != toSequence) {
            long draggedStepId = findBySequence(testcaseId, fromSequence).getId();

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

    @SqlUpdate("update teststep set request = :request, request_type = :requestType, request_filename = :requestFilename, " +
            "updated = CURRENT_TIMESTAMP where id = :teststepId")
    protected abstract int updateRequest(@Bind("teststepId") long teststepId,
                                                           @Bind("request") Object request,
                                                           @Bind("requestType") String requestType,
                                                           @Bind("requestFilename") String requestFilename);

    @Transaction
    public Teststep setRequestFile(long teststepId, String fileName, InputStream inputStream) {
        updateRequest(teststepId, inputStream, TeststepRequestType.FILE.toString(), fileName);

        return findById_NoTransaction(teststepId);
    }

    @SqlQuery("select request from teststep where id = :teststepId")
    public abstract byte[] getBinaryRequestById(@Bind("teststepId") long teststepId);

    @SqlUpdate("update teststep set endpoint_id = null, endpoint_property = 'Endpoint', " +
            "updated = CURRENT_TIMESTAMP where id = :teststepId")
    protected abstract int switchToEndpointProperty(@Bind("teststepId") long teststepId);

    @SqlUpdate("update teststep set endpoint_id = :endpointId, endpoint_property = null, " +
            "updated = CURRENT_TIMESTAMP where id = :teststepId")
    protected abstract int switchToDirectEndpoint(@Bind("teststepId") long teststepId,
                                                  @Bind("endpointId") long endpointId);

    @Transaction
    public void useEndpointProperty(Teststep teststep) {
        switchToEndpointProperty(teststep.getId());
        endpointDAO().deleteUnmanagedEndpointById(teststep.getEndpoint().getId());
    }

    @Transaction
    public void useDirectEndpoint(Teststep teststep, AppMode appMode) throws JsonProcessingException {
        Endpoint endpoint = endpointDAO().createUnmanagedEndpoint_NoTransaction(teststep.getType(), appMode);
        switchToDirectEndpoint(teststep.getId(), endpoint.getId());
    }
}
