package io.irontest.db;

import io.irontest.models.Teststep;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
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
            "intfaceId int, FOREIGN KEY (intfaceId) REFERENCES intface(id), " +
            "endpointId int, FOREIGN KEY (endpointId) REFERENCES endpoint(id), " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE)")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into teststep (testcase_id, name, type, description, request, properties, intfaceId, endpointId) values " +
            "(:testcaseId, :name, :type, :description, :request, :properties, :intfaceId, :endpointId)")
    @GetGeneratedKeys
    public abstract long insert(@Bind("testcaseId") long testcaseId, @Bind("name") String name,
                                @Bind("type") String type, @Bind("description") String description,
                                @Bind("request") String request, @Bind("properties") String properties,
                                @Bind("intfaceId") Long intfaceId, @Bind("endpointId") Long endpointId);

    public long insert(Teststep teststep) throws JsonProcessingException {
        return insert(teststep.getTestcaseId(), teststep.getName(), teststep.getType(), teststep.getDescription(),
                teststep.getRequest(), new ObjectMapper().writeValueAsString(teststep.getProperties()),
                teststep.getIntfaceId() == 0 ? null : teststep.getIntfaceId(),
                teststep.getEndpointId() == 0 ? null : teststep.getEndpointId());
    }

    @SqlUpdate("update teststep set name = :name, description = :description, request = :request, properties = :properties, " +
            "intfaceId = :intfaceId, endpointId = :endpointId, updated = CURRENT_TIMESTAMP where id = :id")
    public abstract int update(@Bind("name") String name, @Bind("description") String description,
                               @Bind("request") String request, @Bind("properties") String properties,
                               @Bind("intfaceId") Long intfaceId, @Bind("endpointId") Long endpointId, @Bind("id") long id);

    public int update(Teststep teststep) throws JsonProcessingException {
        return update(teststep.getName(), teststep.getDescription(), teststep.getRequest(),
                new ObjectMapper().writeValueAsString(teststep.getProperties()),
                teststep.getIntfaceId() == 0 ? null : teststep.getIntfaceId(),
                teststep.getEndpointId() == 0 ? null : teststep.getEndpointId(),
                teststep.getId());
    }

    @SqlUpdate("delete from teststep where id = :id")
    public abstract void deleteById(@Bind("id") long id);

    @SqlQuery("select teststep.*, intface.name as intfaceName, endpoint.name as endpointName from teststep " +
            "left outer join intface on teststep.intfaceId = intface.id left outer join endpoint on teststep.endpointId = endpoint.id " +
            "where teststep.id = :id")
    public abstract Teststep findById(@Bind("id") long id);

    @SqlQuery("select teststep.*, intface.name as intfaceName, endpoint.name as endpointName from teststep " +
            "left outer join intface on teststep.intfaceId = intface.id left outer join endpoint on teststep.endpointId = endpoint.id " +
            "where teststep.testcase_id = :testcaseId")
    public abstract List<Teststep> findByTestcaseId(@Bind("testcaseId") long testcaseId);
}
