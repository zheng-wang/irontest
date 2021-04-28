package io.irontest.db;

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
            "description CLOB, url varchar(1000), host varchar(200), port int, username varchar(200), " +
            "password varchar(500), other_properties CLOB, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (environment_id) REFERENCES environment(id) ON DELETE CASCADE, " +
            "CONSTRAINT ENDPOINT_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(environment_id, type, name))")
    void createTableIfNotExists();

    @SqlUpdate("insert into endpoint (environment_id, type, other_properties) values (:evId, :ep.type, :ep.otherProperties)")
    @GetGeneratedKeys
    long _insertManagedEndpoint(@BindBean("ep") Endpoint endpoint, @Bind("evId") long environmentId);

    @SqlUpdate("update endpoint set name = :name where id = :id")
    void updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @Transaction
    default long insertManagedEndpoint(Endpoint endpoint) {
        long id = _insertManagedEndpoint(endpoint, endpoint.getEnvironment().getId());
        updateNameForInsert(id, "Endpoint " + id);
        return id;
    }

    /**
     * Here assuming endpoint.password is already encrypted.
     * @param endpoint
     * @return
     */
    @SqlUpdate("insert into endpoint (name, type, description, url, host, port, username, password, other_properties) " +
               "values (:ep.name, :ep.type, :ep.description, :ep.url, :ep.host, :ep.port, :ep.username, :ep.password, :ep.otherProperties)")
    @GetGeneratedKeys
    long insertUnmanagedEndpoint(@BindBean("ep") Endpoint endpoint);

    default Endpoint createUnmanagedEndpoint(String teststepType, AppMode appMode) {
        Endpoint endpoint = null;
        if (!Teststep.TYPE_WAIT.equals(teststepType)) {
            endpoint = new Endpoint();
            endpoint.setName("Unmanaged Endpoint");
            switch (teststepType) {
                case Teststep.TYPE_HTTP:
                    endpoint.setType(Endpoint.TYPE_HTTP);
                    break;
                case Teststep.TYPE_SOAP:
                    endpoint.setType(Endpoint.TYPE_SOAP);
                    endpoint.setOtherProperties(new SOAPEndpointProperties());
                    break;
                case Teststep.TYPE_DB:
                    endpoint.setType(Endpoint.TYPE_DB);
                    break;
                case Teststep.TYPE_JMS:
                    endpoint.setType(Endpoint.TYPE_JMS);
                    break;
                case Teststep.TYPE_FTP:
                    endpoint.setType(Endpoint.TYPE_FTP);
                    endpoint.setOtherProperties(new FTPEndpointProperties());
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
                    endpoint.setOtherProperties(new IIBEndpointProperties());
                    break;
                case Teststep.TYPE_AMQP:
                    endpoint.setType(Endpoint.TYPE_AMQP);
                    break;
                case Teststep.TYPE_MQTT:
                    endpoint.setType(Endpoint.TYPE_MQTT);
                    break;
                default:
                    break;
            }

            long id = insertUnmanagedEndpoint(endpoint);
            endpoint.setId(id);
        }

        return endpoint;
    }

    @SqlUpdate("update endpoint set environment_id = :evId, name = :ep.name, type = :ep.type, " +
            "description = :ep.description, url = :ep.url, host = :ep.host, port = :ep.port, " +
            "username = :ep.username, password = CASE " +
                "WHEN COALESCE(password, '') <> COALESCE(:ep.password, '') " + // encrypt only when password is changed
                    "THEN ENCRYPT('AES', '" + ENDPOINT_PASSWORD_ENCRYPTION_KEY + "', STRINGTOUTF8(:ep.password)) " +
                "ELSE password END, " +
            "other_properties = :ep.otherProperties, updated = CURRENT_TIMESTAMP where id = :ep.id")
    void _update(@BindBean("ep") Endpoint endpoint, @Bind("evId") Long environmentId);

    default void update(Endpoint endpoint) {
        Long environmentId = endpoint.getEnvironment() == null ? null : endpoint.getEnvironment().getId();
        _update(endpoint, environmentId);
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
     * Duplicate the endpoint (as an unmanaged one) of the specified test step if the endpoint exists and is an unmanaged one.
     * @param teststepId
     * @return new endpoint id if one endpoint is duplicated; null otherwise.
     */
    @SqlUpdate("insert into endpoint (name, type, url, host, port, username, password, other_properties) " +
            "select e.name, e.type, e.url, e.host, e.port, e.username, e.password, e.other_properties " +
            "from teststep t left outer join endpoint e on t.endpoint_id = e.id where t.id = :teststepId " +
            "and e.id is not null and e.environment_id is null")
    @GetGeneratedKeys
    Long duplicateUnmanagedEndpoint(@Bind("teststepId") long teststepId);

    /**
     * Duplicate the endpoint (as an unmanaged one) of the specified test step if the endpoint exists and is a managed one.
     * @param teststepId
     * @return new endpoint id if one endpoint is duplicated; null otherwise.
     */
    @SqlUpdate("insert into endpoint (name, type, url, host, port, username, password, other_properties) " +
            "select 'Unmanaged Endpoint', e.type, e.url, e.host, e.port, e.username, e.password, e.other_properties " +
            "from teststep t left outer join endpoint e on t.endpoint_id = e.id where t.id = :teststepId " +
            "and e.id is not null and e.environment_id is not null")
    @GetGeneratedKeys
    long duplicateManagedEndpointIntoUnmanaged(@Bind("teststepId") long teststepId);
}
