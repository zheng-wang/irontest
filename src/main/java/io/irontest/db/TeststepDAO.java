package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.Teststep;
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
            "endpoint_id int, FOREIGN KEY (endpoint_id) REFERENCES endpoint(id), " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE)")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into teststep (testcase_id, name, type, description, request, properties, endpoint_id) values " +
            "(:testcaseId, :name, :type, :description, :request, :properties, :endpointId)")
    @GetGeneratedKeys
    public abstract long insert(@Bind("testcaseId") long testcaseId, @Bind("name") String name,
                                @Bind("type") String type, @Bind("description") String description,
                                @Bind("request") String request, @Bind("properties") String properties,
                                @Bind("endpointId") long endpointId);

    public long insert(Teststep teststep) throws JsonProcessingException {
        return insert(teststep.getTestcaseId(), teststep.getName(), teststep.getType(), teststep.getDescription(),
                teststep.getRequest(), new ObjectMapper().writeValueAsString(teststep.getProperties()),
                teststep.getEndpoint().getId());
    }

    @SqlUpdate("update teststep set name = :name, description = :description, request = :request, " +
            "properties = :properties, endpoint_id = :endpointId, " +
            "updated = CURRENT_TIMESTAMP where id = :id")
    public abstract int update(@Bind("name") String name, @Bind("description") String description,
                               @Bind("request") String request, @Bind("properties") String properties,
                               @Bind("id") long id, @Bind("endpointId") long endpointId);

    public int update(Teststep teststep) throws JsonProcessingException {
        return update(teststep.getName(), teststep.getDescription(), teststep.getRequest(),
                new ObjectMapper().writeValueAsString(teststep.getProperties()), teststep.getId(),
                teststep.getEndpoint().getId());
    }

    @SqlUpdate("delete from teststep where id = :id")
    public abstract void deleteById(@Bind("id") long id);

    @SqlQuery("select * from teststep where id = :id")
    public abstract Teststep findById(@Bind("id") long id);

    @SqlQuery("select * from teststep where testcase_id = :testcaseId")
    public abstract List<Teststep> findByTestcaseId(@Bind("testcaseId") long testcaseId);

    @SqlQuery("select id, testcase_id, name, type, description from teststep where testcase_id = :testcaseId")
    public abstract List<Teststep> findByTestcaseId_PrimaryProperties(@Bind("testcaseId") long testcaseId);
}
