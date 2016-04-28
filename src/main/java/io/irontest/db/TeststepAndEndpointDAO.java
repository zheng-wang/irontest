package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.models.Endpoint;
import io.irontest.models.Teststep;
import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.Transaction;

/**
 * Used for cross Teststep and Endpoint transactions.
 * Created by Zheng on 27/04/2016.
 */
public abstract class TeststepAndEndpointDAO {
    @CreateSqlObject
    abstract TeststepDAO teststepDAO();

    @CreateSqlObject
    abstract EndpointDAO endpointDAO();

    @Transaction
    public void createTeststep(Teststep teststep) throws JsonProcessingException {
        long endpointId = endpointDAO().insertUnmanagedEndpoint(teststep.getEndpoint());
        teststep.getEndpoint().setId(endpointId);
        long id = teststepDAO().insert(teststep);
        teststep.setId(id);
    }

    /**
     *
     * @param teststepId
     * @return the teststep with its associated endpoint
     */
    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    public Teststep findTeststepById(long teststepId) {
        Teststep teststep = teststepDAO().findById(teststepId);
        Endpoint endpoint = endpointDAO().findById(teststep.getEndpoint().getId());
        teststep.setEndpoint(endpoint);
        return teststep;
    }

    @Transaction
    public Teststep updateTeststep(Teststep teststep) throws JsonProcessingException {
        Endpoint oldEndpoint = findTeststepById(teststep.getId()).getEndpoint();
        teststepDAO().update(teststep);
        if (teststep.getEndpoint().getEnvironmentId() == null) {    //  this is an unmanaged endpoint, so update it
            endpointDAO().update(teststep.getEndpoint());
        } else if (oldEndpoint.getEnvironmentId() == null) {
            //  delete the old unmanaged endpoint when a managed endpoint is associated with the test step
            endpointDAO().deleteById(oldEndpoint.getId());
        }

        return findTeststepById(teststep.getId());
    }

    @Transaction
    public void deleteTeststep(long teststepId) {
        Teststep teststep = findTeststepById(teststepId);
        teststepDAO().deleteById(teststepId);
        if (teststep.getEndpoint().getEnvironmentId() == null) {  //  delete the teststep's endpoint if it is unmanaged
            endpointDAO().deleteById(teststep.getEndpoint().getId());
        }
    }
}
