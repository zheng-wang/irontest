package io.irontest.db;

import io.irontest.models.Testcase;
import io.irontest.models.Teststep;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Zheng on 1/07/2015.
 */
@RegisterMapper(TestcaseMapper.class)
public abstract class TestcaseDAO {
    @SqlUpdate("create table IF NOT EXISTS testcase (id INT PRIMARY KEY auto_increment, name varchar(200), " +
            "description clob, created timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "updated timestamp DEFAULT CURRENT_TIMESTAMP)")
    public abstract void createTableIfNotExists();

    @CreateSqlObject
    protected abstract TeststepDAO teststepDAO();

    @SqlUpdate("insert into testcase (name, description) values (:name, :description)")
    @GetGeneratedKeys
    public abstract long insert(@BindBean Testcase testcase);

    @SqlUpdate("update testcase set name = :name, description = :description, " +
            "updated = CURRENT_TIMESTAMP where id = :id")
    public abstract int update(@BindBean Testcase testcase);

    @SqlUpdate("delete from testcase where id = :id")
    public abstract void deleteById(@Bind("id") long id);

    @SqlQuery("select * from testcase")
    public abstract List<Testcase> findAll();

    @SqlQuery("select * from testcase where testcase.id = :id")
    protected abstract Testcase _findById(@Bind("id") long id);

    @Transaction
    public Testcase findById(long id) {
        Testcase result = _findById(id);
        List<Teststep> teststeps = teststepDAO().findByTestcaseId_PrimaryProperties(id);
        result.setTeststeps(teststeps);
        return result;
    }
}
