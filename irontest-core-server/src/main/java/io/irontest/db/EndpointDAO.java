package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.AppMode;
import io.irontest.models.endpoint.*;
import io.irontest.models.teststep.Teststep;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;
import static io.irontest.IronTestConstants.ENDPOINT_PASSWORD_ENCRYPTION_KEY;

@RegisterRowMapper(EndpointMapper.class)
public interface EndpointDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS endpoint_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS endpoint (id BIGINT DEFAULT endpoint_sequence.NEXTVAL PRIMARY KEY, " +
            "environment_id int, name varchar(200) NOT NULL DEFAULT CURRENT_TIMESTAMP, type varchar(20) NOT NULL, " +
            "description CLOB, url varchar(1000), username varchar(200), password varchar(500), other_properties CLOB, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (environment_id) REFERENCES environment(id) ON DELETE CASCADE, " +
            "CONSTRAINT ENDPOINT_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(environment_id, type, name))")
    void createTableIfNotExists();

    @SqlUpdate("insert into endpoint (environment_id, type, other_properties) values (:evId, :type, :otherProperties)")
    @GetGeneratedKeys
    long _insertManagedEndpoint(@Bind("evId") long environmentId, @Bind("type") String type,
                                @Bind("otherProperties") String otherProperties);

    @SqlUpdate("update endpoint set name = :name where id = :id")
    void updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @Transaction
    default long insertManagedEndpoint(Endpoint endpoint) throws JsonProcessingException {
        String otherProperties = new ObjectMapper().writeValueAsString(endpoint.getOtherProperties());
        long id = _insertManagedEndpoint(endpoint.getEnvironment().getId(), endpoint.getType(), otherProperties);
        updateNameForInsert(id, "Endpoint " + id);
        return id;
    }

    /**
     * Here assuming endpoint.password is already encrypted.
     * @param endpoint
     * @param otherProperties
     * @return
     */
    @SqlUpdate("insert into endpoint (name, type, description, url, username, password, other_properties) " +
               "values (:ep.name, :ep.type, :ep.description, :ep.url, :ep.username, :ep.password, :otherProperties)")
    @GetGeneratedKeys
    long _insertUnmanagedEndpoint(@BindBean("ep") Endpoint endpoint,
                                  @Bind("otherProperties") String otherProperties);

    default Endpoint createUnmanagedEndpoint(String teststepType, AppMode appMode) throws JsonProcessingException {
        Endpoint endpoint = null;
        if (!Teststep.TYPE_WAIT.equals(teststepType)) {
            endpoint = new Endpoint();
            endpoint.setName("Unmanaged Endpoint");
            switch (teststepType) {
                case Teststep.TYPE_SOAP:
                    endpoint.setType(Endpoint.TYPE_SOAP);
                    endpoint.setOtherProperties(new SOAPEndpointProperties());
                    break;
                case Teststep.TYPE_HTTP:
                    endpoint.setType(Endpoint.TYPE_HTTP);
                    endpoint.setOtherProperties(new HTTPEndpointProperties());
                    break;
                case Teststep.TYPE_DB:
                    endpoint.setType(Endpoint.TYPE_DB);
                    break;
                case Teststep.TYPE_MQ:
                    endpoint.setType(Endpoint.TYPE_MQ);
                    MQEndpointProperties endpointProperties = new MQEndpointProperties();
                    endpointProperties.setConnectionMode(
                            appMode == AppMode.LOCAL ? MQConnectionMode.BINDINGS : MQConnectionMode.CLIENT);
                    endpoint.setOtherProperties(endpointProperties);
                    break;
                case Teststep.TYPE_IIB:
                    endpoint.setType(Endpoint.TYPE_IIB);
                    break;
                default:
                    break;
            }

            long id = insertUnmanagedEndpoint(endpoint);
            endpoint.setId(id);
        }

        return endpoint;
    }

    default long insertUnmanagedEndpoint(Endpoint endpoint) throws JsonProcessingException {
        String otherProperties = new ObjectMapper().writeValueAsString(endpoint.getOtherProperties());
        return _insertUnmanagedEndpoint(endpoint, otherProperties);
    }

    @SqlUpdate("update endpoint set environment_id = :evId, name = :ep.name, type = :ep.type, " +
            "description = :ep.description, url = :ep.url, username = :ep.username, password = CASE " +
                "WHEN COALESCE(password, '') <> COALESCE(:ep.password, '') " + // encrypt only when password is changed
                    "THEN ENCRYPT('AES', '" + ENDPOINT_PASSWORD_ENCRYPTION_KEY + "', STRINGTOUTF8(:ep.password)) " +
                "ELSE password END, " +
            "other_properties = :otherProperties, updated = CURRENT_TIMESTAMP where id = :ep.id")
    void _update(@BindBean("ep") Endpoint endpoint, @Bind("evId") Long environmentId,
                 @Bind("otherProperties") String otherProperties);

    default void update(Endpoint endpoint) throws JsonProcessingException {
        String otherProperties = new ObjectMapper().writeValueAsString(endpoint.getOtherProperties());
        Long environmentId = endpoint.getEnvironment() == null ? null : endpoint.getEnvironment().getId();
        _update(endpoint, environmentId, otherProperties);
    }

    @SqlUpdate("delete from endpoint where id = :id")
    void deleteById(@Bind("id") long id);

    @SqlQuery(
            "select ep.*, ev.name as environment_name " +
            "from endpoint ep left outer join environment ev on ep.environment_id = ev.id " +
            "where ep.id = :id")
    Endpoint findById(@Bind("id") long id);

    @SqlQuery("select id, environment_id, name, type, description from endpoint where environment_id = :environmentId")
    List<Endpoint> findByEnvironmentId_EnvironmentEditView(@Bind("environmentId") long environmentId);

    @SqlQuery(
            "select ep.id, ep.environment_id, ev.name as environment_name, ep.name, ep.type, ep.description " +
            "from endpoint ep left outer join environment ev on ep.environment_id = ev.id " +
            "where ep.type = :endpointType and ep.environment_id is not null")
    List<Endpoint> findManagedEndpointsByType(@Bind("endpointType") String endpointType);

    @SqlUpdate("delete from endpoint where environment_id is null and id = :id")
    void deleteUnmanagedEndpointById(@Bind("id") long id);

    /**
     * Duplicate the endpoint of the specified test step if the endpoint exists and is an unmanaged one.
     * @param oldTeststepId
     * @return new endpoint id if one endpoint is duplicated; null otherwise.
     */
    @SqlUpdate("insert into endpoint (name, type, description, url, username, password, other_properties) " +
            "select e.name, e.type, e.description, e.url, e.username, e.password, e.other_properties " +
            "from teststep t left outer join endpoint e on t.endpoint_id = e.id where t.id = :oldTeststepId " +
            "and e.id is not null and e.environment_id is null")
    @GetGeneratedKeys
    Long duplicateUnmanagedEndpoint(@Bind("oldTeststepId") long oldTeststepId);
}
