package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.Endpoint;
import io.irontest.models.Teststep;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Zheng on 7/07/2015.
 */
@RegisterMapper(TeststepMapper.class)
public abstract class TeststepDAO {
    @SqlUpdate("create table IF NOT EXISTS teststep (" +
            "id INT PRIMARY KEY auto_increment, testcase_id INT, name varchar(200), description clob, " +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "type varchar(20), request clob, properties clob, " +
            "endpoint_id int, FOREIGN KEY (endpoint_id) REFERENCES endpoint(id), " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE)")
    public abstract void createTableIfNotExists();

    @CreateSqlObject
    protected abstract EndpointDAO endpointDAO();

    @SqlUpdate("insert into teststep (testcase_id, name, type, description, request, properties, endpoint_id) values " +
            "(:testcaseId, :name, :type, :description, :request, :properties, :endpointId)")
    @GetGeneratedKeys
    protected abstract long _insert(@Bind("testcaseId") long testcaseId, @Bind("name") String name,
                                @Bind("type") String type, @Bind("description") String description,
                                @Bind("request") String request, @Bind("properties") String properties,
                                @Bind("endpointId") long endpointId);

    @Transaction
    public void insert(Teststep teststep) throws JsonProcessingException {
        long endpointId = endpointDAO().insertUnmanagedEndpoint(teststep.getEndpoint());
        teststep.getEndpoint().setId(endpointId);
        long id = _insert(teststep.getTestcaseId(), teststep.getName(), teststep.getType(), teststep.getDescription(),
                teststep.getRequest(), new ObjectMapper().writeValueAsString(teststep.getProperties()),
                teststep.getEndpoint().getId());
        teststep.setId(id);
    }

    @SqlUpdate("update teststep set name = :name, description = :description, request = :request, " +
            "properties = :properties, endpoint_id = :endpointId, " +
            "updated = CURRENT_TIMESTAMP where id = :id")
    protected abstract int update(@Bind("name") String name, @Bind("description") String description,
                               @Bind("request") String request, @Bind("properties") String properties,
                               @Bind("id") long id, @Bind("endpointId") long endpointId);

    @Transaction
    public Teststep update(Teststep teststep) throws JsonProcessingException {
        Endpoint oldEndpoint = findById(teststep.getId()).getEndpoint();

        update(teststep.getName(), teststep.getDescription(), teststep.getRequest(),
                new ObjectMapper().writeValueAsString(teststep.getProperties()), teststep.getId(),
                teststep.getEndpoint().getId());
        if (teststep.getEndpoint().getEnvironmentId() == null) {    //  this is an unmanaged endpoint, so update it
            endpointDAO().update(teststep.getEndpoint());
        } else if (oldEndpoint.getEnvironmentId() == null) {
            //  delete the old unmanaged endpoint when a managed endpoint is associated with the test step
            endpointDAO().deleteById(oldEndpoint.getId());
        }

        return findById(teststep.getId());
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
        if (teststep.getEndpoint().getEnvironmentId() == null) {  //  delete the teststep's endpoint if it is unmanaged
            endpointDAO().deleteById(teststep.getEndpoint().getId());
        }
    }

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

    @SqlQuery("select * from teststep where testcase_id = :testcaseId")
    public abstract List<Teststep> findByTestcaseId(@Bind("testcaseId") long testcaseId);

    @SqlQuery("select id, testcase_id, name, type, description from teststep where testcase_id = :testcaseId")
    public abstract List<Teststep> findByTestcaseId_PrimaryProperties(@Bind("testcaseId") long testcaseId);
}
