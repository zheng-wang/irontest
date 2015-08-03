package au.com.billon.stt.db;

import au.com.billon.stt.models.Testcase;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Zheng on 1/07/2015.
 */
@RegisterMapper(TestcaseMapper.class)
public interface TestcaseDAO {
    @SqlUpdate("create table IF NOT EXISTS testcase (id INT PRIMARY KEY auto_increment, name varchar(200), description clob, " +
            "environmentId int, created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (environmentId) REFERENCES environment(id))")
    void createTableIfNotExists();

    @SqlUpdate("insert into testcase (name, description, environmentId) values (:name, :description, :environmentId)")
    @GetGeneratedKeys
    long insert(@BindBean Testcase testcase);

    @SqlUpdate("update testcase set name = :name, description = :description, environmentId = :environmentId, updated = CURRENT_TIMESTAMP where id = :id")
    int update(@BindBean Testcase testcase);

    @SqlUpdate("delete from testcase where id = :id")
    void deleteById(@Bind("id") long id);

    @SqlQuery("select testcase.*, " +
            "ENVIRONMENT.NAME as environmentname " +
            "from testcase " +
            "left outer join ENVIRONMENT on testcase.ENVIRONMENTID = ENVIRONMENT.ID ")
    List<Testcase> findAll();

    @SqlQuery("select testcase.*, " +
            "ENVIRONMENT.NAME as environmentname " +
            "from testcase " +
            "left outer join ENVIRONMENT on testcase.ENVIRONMENTID = ENVIRONMENT.ID " +
            "where testcase.id = :id")
    Testcase findById(@Bind("id") long id);
}
