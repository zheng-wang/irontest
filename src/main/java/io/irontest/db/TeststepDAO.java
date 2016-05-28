package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.Endpoint;
import io.irontest.models.Teststep;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

/**
 * Created by Zheng on 7/07/2015.
 */
@RegisterMapper(TeststepMapper.class)
public abstract class TeststepDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS teststep_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS teststep (" +
            "id BIGINT DEFAULT teststep_sequence.NEXTVAL PRIMARY KEY, testcase_id INT NOT NULL, " +
            "sequence SMALLINT NOT NULL, name VARCHAR(200) NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "description CLOB, type VARCHAR(20) NOT NULL, request CLOB, other_properties CLOB, " +
            "created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "endpoint_id INT, FOREIGN KEY (endpoint_id) REFERENCES endpoint(id), " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE, " +
            "CONSTRAINT TESTSTEP_UNIQUE_SEQUENCE_CONSTRAINT UNIQUE(testcase_id, sequence), " +
            "CONSTRAINT TESTSTEP_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(testcase_id, name))")
    public abstract void createTableIfNotExists();

    @CreateSqlObject
    protected abstract EndpointDAO endpointDAO();

    @SqlUpdate("insert into teststep (testcase_id, sequence, type, request, endpoint_id) " +
            "values (:testcaseId, select coalesce(max(sequence), 0) + 1 from teststep where testcase_id = :testcaseId, " +
            ":type, :request, :endpointId)")
    @GetGeneratedKeys
    protected abstract long _insert(@Bind("testcaseId") long testcaseId, @Bind("type") String type,
                                    @Bind("request") String request, @Bind("endpointId") long endpointId);

    @SqlUpdate("update teststep set name = :name where id = :id")
    protected abstract long updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @Transaction
    public void insert(Teststep teststep) throws JsonProcessingException {
        long endpointId = endpointDAO().insertUnmanagedEndpoint(teststep.getEndpoint());
        teststep.getEndpoint().setId(endpointId);

        long id = _insert(teststep.getTestcaseId(), teststep.getType(), teststep.getRequest(),
                teststep.getEndpoint().getId());
        teststep.setId(id);

        String name = "Test Step " + id;
        updateNameForInsert(id, name);
        teststep.setName(name);
    }

    @SqlUpdate("update teststep set name = :name, description = :description, request = :request, " +
            "endpoint_id = :endpointId, other_properties = :otherProperties, " +
            "updated = CURRENT_TIMESTAMP where id = :id")
    protected abstract int _update(@Bind("name") String name, @Bind("description") String description,
                                   @Bind("request") String request, @Bind("id") long id,
                                   @Bind("endpointId") long endpointId,
                                   @Bind("otherProperties") String otherProperties);

    @Transaction
    public Teststep update(Teststep teststep) throws JsonProcessingException {
        Endpoint oldEndpoint = findById_NoTransaction(teststep.getId()).getEndpoint();
        Endpoint newEndpoint = teststep.getEndpoint();

        String otherProperties = teststep.getOtherProperties() == null ?
                null : new ObjectMapper().writeValueAsString(teststep.getOtherProperties());
        _update(teststep.getName(), teststep.getDescription(), teststep.getRequest(), teststep.getId(),
                newEndpoint.getId(), otherProperties);

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

        return findById_NoTransaction(teststep.getId());
    }

    @SqlUpdate("delete from teststep where id = :id")
    protected abstract void _deleteById(@Bind("id") long id);

    @Transaction
    public void deleteById(long id) {
        deleteById_NoTransaction(id);
    }

    public void deleteById_NoTransaction(long id) {
        Teststep teststep = findById_NoTransaction(id);
        _deleteById(id);
        decrementSequenceNumbersOfNextSteps(teststep.getTestcaseId(), (short) (teststep.getSequence() + 1));
        if (!teststep.getEndpoint().isManaged()) {    //  delete the teststep's endpoint if it is unmanaged
            endpointDAO().deleteById(teststep.getEndpoint().getId());
        }
    }

    //  decrement sequence number of all next test steps
    @SqlUpdate("update teststep set sequence = sequence - 1 " +
               "where testcase_id = :testcaseId and sequence >= :startSequenceNumber")
    protected abstract int decrementSequenceNumbersOfNextSteps(@Bind("testcaseId") long testcaseId,
                                                               @Bind("startSequenceNumber") short startSequenceNumber);
    @SqlQuery("select * from teststep where id = :id")
    protected abstract Teststep _findById(@Bind("id") long id);

    /**
     * @param id
     * @return the teststep with its associated endpoint
     */
    @Transaction
    public Teststep findById(long id) {
        return findById_NoTransaction(id);
    }

    public Teststep findById_NoTransaction(long id) {
        Teststep teststep = _findById(id);
        Endpoint endpoint = endpointDAO().findById(teststep.getEndpoint().getId());
        teststep.setEndpoint(endpoint);
        return teststep;
    }

    @SqlQuery("select * from teststep where testcase_id = :testcaseId order by sequence")
    public abstract List<Teststep> _findByTestcaseId(@Bind("testcaseId") long testcaseId);

    @Transaction
    public List<Teststep> findByTestcaseId(@Bind("testcaseId") long testcaseId) {
        List<Teststep> teststeps = _findByTestcaseId(testcaseId);
        EndpointDAO endpointDAO = endpointDAO();
        for (Teststep teststep: teststeps) {
            Endpoint endpoint = endpointDAO.findById(teststep.getEndpoint().getId());
            teststep.setEndpoint(endpoint);
        }
        return teststeps;
    }

    @SqlQuery("select id, testcase_id, sequence, name, type, description from teststep " +
              "where testcase_id = :testcaseId order by sequence")
    public abstract List<Teststep> findByTestcaseId_PrimaryProperties(@Bind("testcaseId") long testcaseId);

    @SqlQuery("select * from teststep where testcase_id = :testcaseId and sequence = :sequence")
    protected abstract Teststep findBySequence(@Bind("testcaseId") long testcaseId, @Bind("sequence") short sequence);

    @SqlUpdate("update teststep set sequence = :newSequence where id = :teststepId")
    protected abstract int updateSequenceById(@Bind("teststepId") long teststepId, @Bind("newSequence") short newSequence);

    @SqlUpdate("update teststep set sequence = case when :direction = 'up' then sequence - 1 else sequence + 1 end " +
            "where testcase_id = :testcaseId and sequence >= :firstSequence and sequence <= :lastSequence")
    protected abstract int batchMoveOneStep(@Bind("testcaseId") long testcaseId,
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
                batchMoveOneStep(testcaseId, (short) (fromSequence + 1), toSequence, "up");
            } else {
                batchMoveOneStep(testcaseId, toSequence, (short) (fromSequence - 1), "down");
            }

            //  move the dragged step last
            updateSequenceById(draggedStepId, toSequence);
        }
    }
}
